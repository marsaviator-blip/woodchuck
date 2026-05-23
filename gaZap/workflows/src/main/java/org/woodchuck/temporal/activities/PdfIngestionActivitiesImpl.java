package org.woodchuck.temporal.activities;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import org.woodchuck.dtos.BibliographyResponse;
import org.woodchuck.dtos.DocumentAnalysisResult;
import org.woodchuck.services.VSmessageSender;

import io.temporal.spring.boot.ActivityImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.StringWriter;
import java.util.UUID;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.StringValue;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;


@Component
@ActivityImpl(taskQueues = "IngestionQueue")
public class PdfIngestionActivitiesImpl implements PdfIngestionActivities {

    private final ChatClient chatClient;
    private final VSmessageSender messageSender;
    private final ObjectMapper objectMapper; // Auto-configured by Spring Boot
    
    public PdfIngestionActivitiesImpl(ChatClient.Builder builder, VSmessageSender messageSender, ObjectMapper objectMapper) {
        this.chatClient = builder.build();
        this.messageSender = messageSender;
        this.objectMapper = objectMapper;
    }

    @Override
    public DocumentAnalysisResult extractReferenceSection(byte[] rawPdfBytes) {
        try {
            File file = File.createTempFile("tempPdf", ".pdf");
            java.nio.file.Files.write(file.toPath(), rawPdfBytes);
        
        try (PDDocument document = Loader.loadPDF(rawPdfBytes)) {
            PDDocumentInformation info = document.getDocumentInformation();
            List<BibliographyResponse.Citation> citations = new ArrayList<>();

            if (info != null) {
                List<BibliographyResponse.BibTeXField> fields = new ArrayList<>();

                // Map standard metadata fields into BibTeX equivalents
                addFieldsIfPresent(fields, "title", info.getTitle());
                addFieldsIfPresent(fields, "author", info.getAuthor());
                addFieldsIfPresent(fields, "subject", info.getSubject());
                addFieldsIfPresent(fields, "keywords", info.getKeywords());
                addFieldsIfPresent(fields, "creator", info.getCreator());
                addFieldsIfPresent(fields, "producer", info.getProducer());

                if (info.getCreationDate() != null) {
                    addFieldsIfPresent(fields, "year", String.valueOf(info.getCreationDate().get(java.util.Calendar.YEAR)));
                }

                // Generate a temporary citation key based on author/year if available
                String citeKey = generateCiteKey(info);

                // Add the single article identity citation to the array
                // For a parsed journal article, the standard BibTeX entry type is usually "article"
                citations.add(new BibliographyResponse.Citation("article", citeKey, fields));
            }
            PDFTextStripper stripper = new PDFTextStripper();
            
            // Step 1: Optimize performance by scanning only the last 4 pages
            int totalPages = document.getNumberOfPages();
            stripper.setStartPage(Math.max(1, totalPages - 3));
            stripper.setEndPage(totalPages);
            
            String rawTailText = stripper.getText(document);

            // Step 2: Extract text starting explicitly at the references heading
            Pattern pattern = Pattern.compile("(?i)\\b(references|bibliography|literature cited)\\b");
            Matcher matcher = pattern.matcher(rawTailText);
            
            if (matcher.find()) {
                // Return everything from the match point to the end of the file
                return new DocumentAnalysisResult(rawTailText.substring(matcher.start()), new BibliographyResponse(citations));
            }
            
            return new DocumentAnalysisResult(rawTailText, new BibliographyResponse(citations)); // Fallback if no clean boundary match found
        }
        } catch (Exception e) {
            throw new RuntimeException("Failed PDFBox extraction for raw byte buffer", e);
        }
    }

    @Override
    public List<String> splitReferences(DocumentAnalysisResult analysisResult) {
        List<String> citations = new ArrayList<>();
        System.out.println(chatClient.toString()); // Debug: Print the ChatClient instance to verify it's properly initialized
        String safeReferenceSection = analysisResult.referenceSection()
                .replace("\\", "\\\\")   
                .replace("\"", "\\\"")   
                .replace("\n", "\\n")    
                .replace("\r", "\\r");   
        System.out.println("Prepared reference section for ChatClient: " + safeReferenceSection); // Debug output
        BibliographyResponse chatResponse = chatClient.prompt()
                .user("""
              Convert this reference text into structured fields matching the JSON schema.
              
              CRITICAL: Cleanly extract the book/article titles. If a title contains inner quote marks, change them to single quotes (e.g., 'Optimization methods') to keep the JSON valid.
              
              Text to process:
              """ + safeReferenceSection)
                .call()
                .entity(BibliographyResponse.class); // Automatically maps JSON to your Java object
                System.out.println("ChatClient response for splitting references: " + chatResponse); // Debug output
                if(analysisResult.bibliography() == null || analysisResult.bibliography().citations() == null || analysisResult.bibliography().citations().isEmpty() || analysisResult.bibliography().citations().get(0) == null) {
                    messageSender.sendBibtexMessage(chatResponse); // Debug: Send the raw response to Kafka for inspection
                    return citations; // Return empty list if no parent citation exists to attach to
                }
                BibliographyResponse.Citation updatedParent = analysisResult.bibliography().citations().get(0).withChildren(chatResponse.citations());

                messageSender.sendBibtexMessage(new BibliographyResponse(List.of(updatedParent))); // Debug: Send the raw response to Kafka for inspection
        
        return citations;
    }

    @Override
    public void saveToNeo4jGraph(String title, List<String> crossRefJsonRecords) {
        // 1. Initialize a JBibTeX database container
        BibTeXDatabase bibDatabase = new BibTeXDatabase();

        for (String jsonStr : crossRefJsonRecords) {
            try {
                JsonNode root = objectMapper.readTree(jsonStr);
                JsonNode message = root.path("message");

                String doi = message.path("DOI").asText("");
                if (doi.isEmpty()) continue;

                // Determine the structural entry type (e.g., @article, @book)
                String typeStr = message.path("type").asText("journal-article");
                Key typeKey = typeStr.contains("book") ? BibTeXEntry.TYPE_BOOK : BibTeXEntry.TYPE_ARTICLE;

                // Create a deterministic citation key using the DOI string
                Key citationKey = new Key(doi.replaceAll("[^a-zA-Z0-9]", ""));

                // Initialize the structural JBibTeX Entry
                BibTeXEntry entry = new BibTeXEntry(typeKey, citationKey);

                // Map CrossRef title array
                String citationTitle = message.path("title").path(0).asText("Unknown Title");
                entry.addField(BibTeXEntry.KEY_TITLE, new StringValue(citationTitle, StringValue.Style.QUOTED));

                // Map publication year
                JsonNode dateParts = message.path("published").path("date-parts").path(0);
                String year = dateParts.has(0) ? dateParts.get(0).asText() : "Unknown Year";
                entry.addField(BibTeXEntry.KEY_YEAR, new StringValue(year, StringValue.Style.QUOTED));

                // Map authors list
                List<String> authorNames = new ArrayList<>();
                JsonNode authorsNode = message.path("author");
                if (authorsNode.isArray()) {
                    for (JsonNode author : authorsNode) {
                        String family = author.path("family").asText("");
                        String given = author.path("given").asText("");
                        if (!family.isEmpty()) {
                            authorNames.add(family + ", " + given);
                        }
                    }
                }
                if (!authorNames.isEmpty()) {
                    String authorsString = String.join(" and ", authorNames); // BibTeX standards separate authors with 'and'
                    entry.addField(BibTeXEntry.KEY_AUTHOR, new StringValue(authorsString, StringValue.Style.QUOTED));
                }

                // Add to JBibTeX collection
                bibDatabase.addObject(entry);

            } catch (Exception e) {
                System.err.println("Failed parsing to JBibTeX format: " + e.getMessage());
            }
        }

        // 2. Persist the collection directly into Neo4j
        persistJBibtexDatabase(title, bibDatabase);
    }

    private String cleanWhitespace(String text) {
        return text.replaceAll("\\s*\\n\\s*", " ").trim();
    }

    private void persistJBibtexDatabase(String parentTitle, BibTeXDatabase db) {
        Map<Key, BibTeXEntry> entries = db.getEntries();
        if (entries.isEmpty()) return;


    }

    private void addFieldsIfPresent(List<BibliographyResponse.BibTeXField> fields, String key, String value) {
        if (value != null && !value.strip().isEmpty()) {
            fields.add(new BibliographyResponse.BibTeXField(key, value.strip()));
        }
    }

    private String generateCiteKey(PDDocumentInformation info) {
        String author = info.getAuthor();
        String year = "";
        
        if (info.getCreationDate() != null) {
            year = String.valueOf(info.getCreationDate().get(java.util.Calendar.YEAR));
        }

        if (author != null && !author.strip().isEmpty()) {
            // Take the first word of the author string (usually last name) and strip special characters
            String cleanAuthor = author.split("\\s+")[0].replaceAll("[^a-zA-Z0-9]", "");
            return cleanAuthor.toLowerCase() + year;
        }
        
        return "doc" + (year.isEmpty() ? System.currentTimeMillis() : year);
    }

}

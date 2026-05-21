package org.woodchuck.temporal.activities;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.woodchuck.dtos.BibliographyResponse;
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
    public String extractReferenceSection(String pdfFilePath) {
        try {
            Resource resource = new FileSystemResource(pdfFilePath);
            File file = resource.getFile();
        
        try (PDDocument document = Loader.loadPDF(file)) {
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
                return rawTailText.substring(matcher.start());
            }
            
            return rawTailText; // Fallback if no clean boundary match found
        }
        } catch (Exception e) {
            throw new RuntimeException("Failed PDFBox extraction for file: " + pdfFilePath, e);
        }
    }

    @Override
    public List<String> splitReferences(String rawReferenceSection) {
        List<String> citations = new ArrayList<>();
        System.out.println(chatClient.toString()); // Debug: Print the ChatClient instance to verify it's properly initialized
        String safeReferenceSection = rawReferenceSection
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
                messageSender.sendBibtexMessage(chatResponse); // Debug: Send the raw response to Kafka for inspection
        // // Match standard academic list numbers like [1], 1., or [Buchberger85]
        // Pattern pattern = Pattern.compile("(^|\\n)(\\[\\d+\\]|\\d+\\.\\s+|\\[[A-Za-z]+\\d+\\])");
        // Matcher matcher = pattern.matcher(rawReferenceSection);
        
        // int lastIndex = 0;
        // String currentCitation = "";
        
        // while (matcher.find()) {
        //     if (lastIndex != 0) {
        //         currentCitation = rawReferenceSection.substring(lastIndex, matcher.start()).trim();
        //         if (!currentCitation.isEmpty()) {
        //             citations.add(cleanWhitespace(currentCitation));
        //         }
        //     }
        //     lastIndex = matcher.end();
        // }
        
        // // Add the very last citation entry remaining in the string block
        // if (lastIndex < rawReferenceSection.length()) {
        //     currentCitation = rawReferenceSection.substring(lastIndex).trim();
        //     if (!currentCitation.isEmpty()) {
        //         citations.add(cleanWhitespace(currentCitation));
        //     }
        // }
        
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

       //messageSender.sendMessage(parentTitle);
        
        // try (Session session = neo4jDriver.session()) {
        //     String parentId = UUID.randomUUID().toString();

        //     session.executeWrite(tx -> {
        //         // Initialize the main parent article node
        //         tx.run("""
        //             MERGE (p:Document {title: $parentTitle})
        //             ON CREATE SET p.id = $parentId, p.type = 'MainArticle'
        //             """, Map.of("parentTitle", parentTitle, "parentId", parentId));

        //         // Loop through your JBibTeX entries
        //         for (Map.Entry<Key, BibTeXEntry> entryMapping : entries.entrySet()) {
        //             BibTeXEntry entry = entryMapping.getValue();

        //             // Safely pull string values using JBibTeX Key lookups
        //             String citationKey = entryMapping.getKey().getValue();
        //             String title = getBibtexFieldValue(entry, BibTeXEntry.KEY_TITLE);
        //             String authors = getBibtexFieldValue(entry, BibTeXEntry.KEY_AUTHOR);
        //             String year = getBibtexFieldValue(entry, BibTeXEntry.KEY_YEAR);

        //             org.jbibtex.BibTeXDatabase tempDb = new org.jbibtex.BibTeXDatabase();
        //             tempDb.addObject(entry); 
        //             // Use the built-in JBibTeX formatter to generate a compliant output block
        //             StringWriter writer = new StringWriter();
        //             org.jbibtex.BibTeXFormatter formatter = new org.jbibtex.BibTeXFormatter();
        //             try {
        //                 formatter.format(tempDb, writer);
        //             } catch (Exception ignored) {}
        //             String rawBibtexBlock = writer.toString();

        //             // Relate everything together in the graph
        //             tx.run("""
        //                 MATCH (p:Document {title: $parentTitle})
        //                 MERGE (c:Citation {citationKey: $citationKey})
        //                 ON CREATE SET 
        //                     c.title = $title,
        //                     c.authors = $authors,
        //                     c.year = $year,
        //                     c.rawBibtex = $rawBibtex
        //                 MERGE (p)-[:CITES]->(c)
        //                 """, Map.of(
        //                     "parentTitle", parentTitle,
        //                     "citationKey", citationKey,
        //                     "title", title,
        //                     "authors", authors,
        //                     "year", year,
        //                     "rawBibtex", rawBibtexBlock
        //                 ));
        //         }
        //         return null;
        //     });
        // }
    }

}

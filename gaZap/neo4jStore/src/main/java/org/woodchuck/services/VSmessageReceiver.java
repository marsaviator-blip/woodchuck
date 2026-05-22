package org.woodchuck.services;

import java.io.StringWriter;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import ai.docling.serve.api.convert.request.source.HttpSource;
import org.woodchuck.dtos.BibliographyResponse;
import ai.docling.serve.api.chunk.request.HybridChunkDocumentRequest;
import ai.docling.serve.api.chunk.response.ChunkDocumentResponse;
import ai.docling.serve.api.convert.request.ConvertDocumentRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.StringValue;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

@Service
public class VSmessageReceiver {
    
    private static final Logger logger = LoggerFactory.getLogger(VSmessageReceiver.class);
    private static final String TOPIC_NAME = "woodchuck";
    private static final String BIBTEX_TOPIC_NAME = "woodchuck-bibtex";
    private final DoclingAsyncService doclingAsyncService;
    private final Driver neo4jDriver;

    public VSmessageReceiver(DoclingAsyncService doclingAsyncService, Driver neo4jDriver){
        this.doclingAsyncService = doclingAsyncService;
        this.neo4jDriver = neo4jDriver;
    }

    @KafkaListener(topics = TOPIC_NAME)
    public void receiveMessage(String url) {
        logger.info("Received message: {}", url);
        // Process the message as needed
        CompletableFuture<ChunkDocumentResponse> future = doclingAsyncService.processDocumentAsync(
                HybridChunkDocumentRequest.builder()
                        .source(HttpSource.builder().url(URI.create(url)).build())
                        .build());
    }

    @KafkaListener(topics = BIBTEX_TOPIC_NAME)
    public void receiveBibtexResponse(BibliographyResponse response) {
        logger.info("Received BibTeX message: {}", response);
        BibTeXDatabase database = new BibTeXDatabase();

        // Map over each citation from your record list
        for (BibliographyResponse.Citation citation : response.citations()) {
            
            // 1. Create Keys for the entry type and the unique citation key
            Key entryTypeKey = new Key(citation.type()); // e.g., "article", "book"
            Key citeKey = new Key(citation.citeKey());   // e.g., "smith2026"

            // 2. Initialize the BibTeXEntry container
            BibTeXEntry entry = new BibTeXEntry(entryTypeKey, citeKey);

            // 3. Map your BibTeXField fields directly onto the entry object
            for (BibliographyResponse.BibTeXField field : citation.fields()) {
                Key fieldKey = new Key(field.key()); // e.g., "title", "author"
                
                // JBibTeX requires values wrapped in specific Value subclasses (like StringValue)
                StringValue fieldValue = new StringValue(
                    field.value(), 
                    StringValue.Style.QUOTED // Encapsulates content in "" rather than {}
                );

                entry.addField(fieldKey, fieldValue);
            }

            // 4. Register the populated entry into the database instance
            database.addObject(entry);
        }

        Map<Key, BibTeXEntry> entries = database.getEntries();
        if (entries.isEmpty()) return;

        try (Session session = neo4jDriver.session()) {
            String parentId = UUID.randomUUID().toString();
            String parentTitle = "Document with " + entries.size() + " citations";

            session.executeWrite(tx -> {
                // Initialize the main parent article node
                tx.run("""
                    MERGE (p:Document {title: $parentTitle})
                    ON CREATE SET p.id = $parentId, p.type = 'MainArticle'
                    """, Map.of("parentTitle", parentTitle, "parentId", parentId));

                // Loop through your JBibTeX entries
                for (Map.Entry<Key, BibTeXEntry> entryMapping : entries.entrySet()) {
                    BibTeXEntry entry = entryMapping.getValue();

                    // Safely pull string values using JBibTeX Key lookups
                    String citationKey = entryMapping.getKey().getValue();
                    String title = getBibtexFieldValue(entry, BibTeXEntry.KEY_TITLE);
                    String authors = getBibtexFieldValue(entry, BibTeXEntry.KEY_AUTHOR);
                    String year = getBibtexFieldValue(entry, BibTeXEntry.KEY_YEAR);

                    org.jbibtex.BibTeXDatabase tempDb = new org.jbibtex.BibTeXDatabase();
                    tempDb.addObject(entry); 
                    // Use the built-in JBibTeX formatter to generate a compliant output block
                    StringWriter writer = new StringWriter();
                    org.jbibtex.BibTeXFormatter formatter = new org.jbibtex.BibTeXFormatter();
                    try {
                        formatter.format(tempDb, writer);
                    } catch (Exception ignored) {}
                    String rawBibtexBlock = writer.toString();

                    // Relate everything together in the graph
                    tx.run("""
                        MATCH (p:Document {title: $parentTitle})
                        MERGE (c:Citation {citationKey: $citationKey})
                        ON CREATE SET 
                            c.title = $title,
                            c.authors = $authors,
                            c.year = $year,
                            c.rawBibtex = $rawBibtex
                        MERGE (p)-[:CITES]->(c)
                        """, Map.of(
                            "parentTitle", parentTitle,
                            "citationKey", citationKey,
                            "title", title,
                            "authors", authors,
                            "year", year,
                            "rawBibtex", rawBibtexBlock
                        ));
                }
                return null;
            });
        }
    }

    private String getBibtexFieldValue(BibTeXEntry entry, Key fieldKey) {
        org.jbibtex.Value val = entry.getField(fieldKey);
        return (val != null) ? val.toUserString() : "Unknown";
    }

}

package org.woodchuck.services;

import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import ai.docling.serve.api.convert.request.source.HttpSource;
import org.woodchuck.dtos.BibliographyResponse;
import ai.docling.serve.api.chunk.request.HybridChunkDocumentRequest;
import ai.docling.serve.api.chunk.request.options.HybridChunkerOptions;

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
import org.neo4j.driver.TransactionContext;

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
        doclingAsyncService.processDocumentAsync(
                HybridChunkDocumentRequest.builder()
                        .source(HttpSource.builder().url(URI.create(url)).build())
                        .chunkingOptions(HybridChunkerOptions.builder()
                            .mergePeers(true)   
                            .build())
                        .build());
    }

    @KafkaListener(topics = BIBTEX_TOPIC_NAME)
    public void receiveBibtexResponse(BibliographyResponse response) {
        logger.info("Received BibTeX message: {}", response);
        if (response == null || response.citations() == null || response.citations().isEmpty()) return;

        try (Session session = neo4jDriver.session()) {
            String parentId = UUID.randomUUID().toString();
            String parentTitle = "Document citations " + parentId;

            session.executeWrite(tx -> {
                // Initialize the main parent article node
                tx.run("""
                    MERGE (p:Document {title: $parentTitle})
                    ON CREATE SET p.id = $parentId, p.type = 'MainArticle'
                    """, Map.of("parentTitle", parentTitle, "parentId", parentId));

                persistCitationsRecursively(tx, parentTitle, response.citations(), null);
                return null;
            });
        }
    }

    private void persistCitationsRecursively(
            TransactionContext tx,
            String parentTitle,
            List<BibliographyResponse.Citation> citations,
            String parentCitationKey) {
        if (citations == null || citations.isEmpty()) {
            return;
        }

        for (BibliographyResponse.Citation citation : citations) {
            if (citation == null || citation.citeKey() == null || citation.citeKey().isBlank()) {
                continue;
            }

            BibTeXEntry entry = toBibtexEntry(citation);
            String citationKey = citation.citeKey();
            String title = getBibtexFieldValue(entry, BibTeXEntry.KEY_TITLE);
            String authors = getBibtexFieldValue(entry, BibTeXEntry.KEY_AUTHOR);
            String year = getBibtexFieldValue(entry, BibTeXEntry.KEY_YEAR);
            String rawBibtexBlock = formatRawBibtex(entry);

            tx.run("""
                MERGE (c:Citation {citationKey: $citationKey})
                ON CREATE SET
                    c.title = $title,
                    c.authors = $authors,
                    c.year = $year,
                    c.rawBibtex = $rawBibtex
                """, Map.of(
                    "citationKey", citationKey,
                    "title", title,
                    "authors", authors,
                    "year", year,
                    "rawBibtex", rawBibtexBlock
                ));

            if (parentCitationKey == null) {
                tx.run("""
                    MATCH (p:Document {title: $parentTitle})
                    MATCH (c:Citation {citationKey: $citationKey})
                    MERGE (p)-[:CITES]->(c)
                    """, Map.of("parentTitle", parentTitle, "citationKey", citationKey));
            } else {
                tx.run("""
                    MATCH (parent:Citation {citationKey: $parentCitationKey})
                    MATCH (child:Citation {citationKey: $citationKey})
                    MERGE (parent)-[:CITES]->(child)
                    """, Map.of("parentCitationKey", parentCitationKey, "citationKey", citationKey));
            }

            List<BibliographyResponse.Citation> children = citation.children();
            if (children != null) {
                persistCitationsRecursively(tx, parentTitle, children, citationKey);
            }
        }
    }

    private BibTeXEntry toBibtexEntry(BibliographyResponse.Citation citation) {
        String type = (citation.type() == null || citation.type().isBlank()) ? "misc" : citation.type();
        Key entryTypeKey = new Key(type);
        Key citeKey = new Key(citation.citeKey());
        BibTeXEntry entry = new BibTeXEntry(entryTypeKey, citeKey);

        List<BibliographyResponse.BibTeXField> fields = citation.fields();
        if (fields == null || fields.isEmpty()) {
            return entry;
        }

        for (BibliographyResponse.BibTeXField field : fields) {
            if (field == null || field.key() == null || field.key().isBlank() || field.value() == null) {
                continue;
            }

            Key fieldKey = new Key(field.key());
            StringValue fieldValue = new StringValue(field.value(), StringValue.Style.QUOTED);
            entry.addField(fieldKey, fieldValue);
        }

        return entry;
    }

    private String formatRawBibtex(BibTeXEntry entry) {
        BibTeXDatabase tempDb = new BibTeXDatabase();
        tempDb.addObject(entry);
        StringWriter writer = new StringWriter();
        org.jbibtex.BibTeXFormatter formatter = new org.jbibtex.BibTeXFormatter();
        try {
            formatter.format(tempDb, writer);
        } catch (Exception ignored) {
            return "";
        }
        return writer.toString();
    }

    private String getBibtexFieldValue(BibTeXEntry entry, Key fieldKey) {
        org.jbibtex.Value val = entry.getField(fieldKey);
        return (val != null) ? val.toUserString() : "Unknown";
    }

}

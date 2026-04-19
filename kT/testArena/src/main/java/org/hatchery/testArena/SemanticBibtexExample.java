package org.hatchery.testArena;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.Key;
import org.jbibtex.Value;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

public class SemanticBibtexExample {

    public static void main(String[] args) {
        String query = "graph neural networks for molecular property prediction";
        String bibtexCorpus = sampleBibtexCorpus();

        List<Document> documents = parseBibtexDocuments(bibtexCorpus);
        VectorStore vectorStore = buildAndPopulateVectorStore(documents);
        List<Document> topCandidates = similaritySearch(vectorStore, query, 3);

        String promptText = buildSemanticSearchPrompt(query, topCandidates, 3);

        System.out.println("Top semantic candidates:");
        for (int i = 0; i < topCandidates.size(); i++) {
            Document candidate = topCandidates.get(i);
            String key = String.valueOf(candidate.getMetadata().get("citationKey"));
            String bibtex = String.valueOf(candidate.getMetadata().get("bibtex"));
            System.out.println("\nCandidate " + (i + 1) + " [" + key + "]:\n" + bibtex);
        }

        System.out.println("\n--- Spring AI Prompt For Final Ranking ---\n");
        System.out.println(promptText);
    }

    public static String buildSemanticSearchPrompt(String query, List<Document> candidates, int topK) {
        String template = """
            You are a research assistant specialized in bibliographic retrieval.

            Task:
            - User query: {query}
            - Select exactly {topK} most semantically relevant BibTeX entries.
            - Return them in ranked order and provide one-line reasoning for each.

            Candidate BibTeX entries:
            {entries}

            Output format:
            1) <bibtex_key> - <reason>
            2) <bibtex_key> - <reason>
            ...
            """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        String candidateBibtex = candidates.stream()
            .map(doc -> String.valueOf(doc.getMetadata().get("bibtex")))
            .collect(Collectors.joining("\n\n"));

        Prompt prompt = promptTemplate.create(Map.of(
            "query", query,
            "topK", Integer.toString(topK),
            "entries", candidateBibtex
        ));

        return prompt.getContents();
    }

    public static VectorStore buildAndPopulateVectorStore(List<Document> entries) {
        EmbeddingModel embeddingModel = new LocalHashEmbeddingModel(256);
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        vectorStore.add(entries);

        return vectorStore;
    }

    public static List<Document> similaritySearch(VectorStore vectorStore, String query, int topK) {
        SearchRequest searchRequest = SearchRequest.builder()
            .query(query)
            .topK(Math.max(1, topK))
            .similarityThreshold(0.0)
            .build();

        return vectorStore.similaritySearch(searchRequest);
    }

    /**
     * Lightweight deterministic embedding model for local demos.
     * Avoids external model loading while still exercising Spring AI VectorStore APIs.
     */
    static final class LocalHashEmbeddingModel implements EmbeddingModel {
        private final int dimensions;

        LocalHashEmbeddingModel(int dimensions) {
            this.dimensions = Math.max(64, dimensions);
        }

        @Override
        public EmbeddingResponse call(EmbeddingRequest request) {
            List<String> texts = request.getInstructions();
            List<Embedding> embeddings = new ArrayList<>(texts.size());

            for (int i = 0; i < texts.size(); i++) {
                embeddings.add(new Embedding(embedText(texts.get(i)), i));
            }

            return new EmbeddingResponse(embeddings);
        }

        @Override
        public float[] embed(Document document) {
            return embedText(document == null ? "" : document.getText());
        }

        private float[] embedText(String text) {
            float[] vector = new float[this.dimensions];
            if (text == null || text.isBlank()) {
                return vector;
            }

            byte[] bytes = text.toLowerCase().getBytes(StandardCharsets.UTF_8);
            for (byte b : bytes) {
                int value = b & 0xff;
                int index = value % this.dimensions;
                vector[index] += 1.0f;
            }

            float norm = 0.0f;
            for (float v : vector) {
                norm += v * v;
            }

            if (norm == 0.0f) {
                return vector;
            }

            float denom = (float) Math.sqrt(norm);
            for (int i = 0; i < vector.length; i++) {
                vector[i] = vector[i] / denom;
            }

            return vector;
        }
    }

    private static List<Document> parseBibtexDocuments(String corpus) {
        try {
            BibTeXParser parser = new BibTeXParser();
            BibTeXDatabase database = parser.parse(new StringReader(corpus));

            List<Document> result = new ArrayList<>();
            for (BibTeXEntry entry : database.getEntries().values()) {
                result.add(toDocument(entry));
            }
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse BibTeX corpus with jBibTeX", e);
        }
    }

    private static Document toDocument(BibTeXEntry entry) {
        String citationKey = entry.getKey().toString();
        String bibtex = formatEntry(entry);
        String searchableText = buildSearchableText(entry, bibtex);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("citationKey", citationKey);
        metadata.put("bibtex", bibtex);

        return new Document(searchableText, metadata);
    }

    private static String buildSearchableText(BibTeXEntry entry, String bibtex) {
        String title = getFieldValue(entry, "title");
        String abstractText = getFieldValue(entry, "abstract");

        // For semantic search quality, embed only the core topical fields.
        return String.join(" ", title, abstractText).trim();
    }

    private static String getFieldValue(BibTeXEntry entry, String fieldName) {
        Value value = entry.getFields().get(new Key(fieldName));
        return value == null ? "" : value.toString();
    }

    private static String formatEntry(BibTeXEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(entry.getType()).append("{").append(entry.getKey()).append(",\n");

        for (Map.Entry<Key, Value> field : entry.getFields().entrySet()) {
            sb.append("  ")
                .append(field.getKey())
                .append("={")
                .append(field.getValue())
                .append("},\n");
        }

        sb.append("}");
        return sb.toString();
    }

    private static String sampleBibtexCorpus() {
        return """
            @article{gilmer2017neural,
              title={Neural message passing for quantum chemistry},
              author={Gilmer, Justin and Schoenholz, Samuel and Riley, Patrick and Vinyals, Oriol and Dahl, George},
              journal={International Conference on Machine Learning},
              year={2017}
            }

            @article{kearnes2016molecular,
              title={Molecular graph convolutions: moving beyond fingerprints},
              author={Kearnes, Steven and McCloskey, Kevin and Berndl, Marc and Pande, Vijay and Riley, Patrick},
              journal={Journal of Computer-Aided Molecular Design},
              year={2016}
            }

            @article{wu2021surveygnn,
              title={A comprehensive survey on graph neural networks},
              author={Wu, Zonghan and Pan, Shirui and Chen, Fengwen and Long, Guodong and Zhang, Chengqi and Yu, Philip},
              journal={IEEE Transactions on Neural Networks and Learning Systems},
              year={2021}
            }

            @article{vaswani2017attention,
              title={Attention is all you need},
              author={Vaswani, Ashish and Shazeer, Noam and Parmar, Niki and Uszkoreit, Jakob and Jones, Llion and Gomez, Aidan and Kaiser, Lukasz and Polosukhin, Illia},
              journal={Advances in Neural Information Processing Systems},
              year={2017}
            }

            @article{hamilton2017inductive,
              title={Inductive representation learning on large graphs},
              author={Hamilton, William and Ying, Rex and Leskovec, Jure},
              journal={Advances in Neural Information Processing Systems},
              year={2017}
            }
            """;
    }
}

// From project base run me like this:
// mvn exec:java -Dexec.mainClass="org.hatchery.testArena.SemanticBibtexExample"

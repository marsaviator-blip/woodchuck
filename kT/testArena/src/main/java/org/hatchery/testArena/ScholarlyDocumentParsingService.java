package org.hatchery.testArena;

@Service
public class ScholarlyDocumentParsingService {

    private final ChatModel chatModel;
    // Instantiate directly to keep it scoped to this specific extraction task
    private final BeanOutputConverter<ScholarlyDocumentMetadata> outputConverter = 
            new BeanOutputConverter<>(ScholarlyDocumentMetadata.class);

    public ScholarlyDocumentParsingService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public ScholarlyDocumentMetadata extractMetadata(String documentText) {
        String promptTemplate = """
                Extract metadata from this document.
                {format}
                Document: {documentText}
                """;

        Prompt prompt = new PromptTemplate(promptTemplate)
                .create(Map.of(
                        "format", outputConverter.getFormat(),
                        "documentText", documentText
                ));

        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return outputConverter.convert(response);
    }
}


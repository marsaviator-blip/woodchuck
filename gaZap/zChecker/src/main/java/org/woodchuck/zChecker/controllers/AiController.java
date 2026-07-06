package org.woodchuck.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;

@RestController
public class AiController {

    private final ChatClient chatClient;

    // Spring Boot auto-configures the VectorStore bean based on your starter dependency (e.g., pgvector, elasticsearch)
    public AiController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                    // Automatically intercepts the prompt, does a similarity search, and appends context
                    QuestionAnswerAdvisor.builder(vectorStore)
                        //.similarityThreshold(0.6) // Filters out weakly related content
                        .build()
                )
                .build();
    }

    @GetMapping("/ai/rag")
    public String askWithContext(@RequestParam String question) {
        return this.chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}

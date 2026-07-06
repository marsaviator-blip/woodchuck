package org.woodchuck.zChecker.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.ai.vectorstore.filter.Filter;

@Service
public class ScholarlyRagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ScholarlyRagService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        // Build a base ChatClient that can be customized dynamically per request
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Answers a research question using ONLY context matching the specified author and funding org.
     */
    public String answerWithFilters(String userQuestion, String fundingOrg, String authorName) {
//        FilterExpressionBuilder b = new FilterExpressionBuilder();

        // 1. Define the tight metadata boundary
        // var filterExpression = b.and(
        //     b.eq("fundingInstitutions", fundingOrg),
        //     b.eq("authors", authorName)
        // );
        // Filter.Expression filterExpression = b.and(
        //     b.eq("fundingInstitutions", fundingOrg),
        //     b.eq("authors", authorName)
        // ).build();
String filterText = String.format("fundingInstitutions == '%s' && authors == '%s'", fundingOrg, authorName);

        // 2. Configure the background vector retriever options
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(4)
                .similarityThreshold(0.5)
                //.filterExpression(filterExpression)
                .filterExpression(filterText)
                .build();

        var questionAnswerAdvisor = QuestionAnswerAdvisor.builder(this.vectorStore)
                .searchRequest(searchRequest)
                .build();
        // 3. Bind the filtered advisor and stream/execute the chat model
        return this.chatClient.mutate()
                // The QuestionAnswerAdvisor automatically merges filtered search results into the prompt context
                .defaultAdvisors(questionAnswerAdvisor)
                .build()
                .prompt()
                .user(userQuestion)
                .call()
                .content(); // Returns the generated synthesis string
    }
}

/*
String customSystemTemplate = """
    You are an academic assistant. Answer the question using ONLY the provided text snippets.
    If the snippets do not contain the answer, state that you cannot find the info within 
    the papers matching this specific author or funding source. Do not use outside knowledge.
    
    Context:
    {question_answer_context}
    """;

QuestionAnswerAdvisor strictAdvisor = new QuestionAnswerAdvisor(
        vectorStore, 
        searchRequest, 
        customSystemTemplate
);
 */

package org.woodchuck.zChecker.controllers;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.MediaType;

import reactor.core.publisher.Flux;

import org.woodchuck.zChecker.dtos.ChatMessageRequest;

@RestController
@RequestMapping("/api/chat")
//@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3002"}) // Allowing CORS for both the Angular and React frontends
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    //Injecting the auto-configured builder provided by Spring AI
    public ChatController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
       this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.chatMemory = chatMemory;}

    @PostMapping(value="/postStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> handleChatStream(@RequestBody ChatMessageRequest request) {
        // Reads directly out of the {"prompt": "..."} request body payload
        String userPrompt = (String) request.getPrompt();
        String userId = (String) request.getUserId(); // Differentiate users in your team
        System.out.println("Received incoming prompt: " + userPrompt);
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            System.out.println("ERROR: Received empty prompt in request body.");
            return Flux.just(ServerSentEvent.<String>builder().data("Error: Prompt cannot be empty.").build());
        }

        var memoryAdvisor = MessageChatMemoryAdvisor.builder(this.chatMemory)
            // .conversationId(userId) // Binds your user tracking ID
            // .chatHistoryWindowSize(10) // Sets sliding context window to 10
            .build();

        // Your AI generation logic goes here
        return this.chatClient.prompt()
                .user(userPrompt)
                .advisors(memoryAdvisor) // Pass the built advisor directly
                .advisors(advisorSpec -> advisorSpec
                    .param("chat_memory_conversation_id", userId)
                    .param("chat_memory_retrieve_size", 10))
                .stream()
                .content()
                .map(token -> ServerSentEvent.<String>builder().data(token).build());
        // return this.chatClient.prompt()
        //         .user(userPrompt)
        //         .advisors(new MessageChatMemoryAdvisor(this.chatMemory, userId, 10)) 
        //         .stream() // Enables token-by-token streaming
        //         .content()
        //         .map(token -> ServerSentEvent.<String>builder()
        //             .data(token)
        //             .build());
    }

    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<String> streamChat(@RequestParam String message) {
        System.out.println("Received message: " + message); 
        return this.chatClient.prompt()
                .user(message)
                .stream() // Enables token-by-token streaming
                .content();
        // try {
        //     return this.chatClient.prompt()
        //         .user(message)
        //         .stream()
        //         .response() // Use .response() instead of .content() to see metadata
        //         .map(chatResponse -> {
        //             // Check if the current token is part of the "thinking" block
        //             var generation = chatResponse.getResult().getOutput();
        //             if (generation.getMetadata() != null && 
        //                 Boolean.TRUE.equals(generation.getMetadata().get("thinking"))) {
        //                 return "[Thinking: " + generation.getText() + "]";
        //             }
        //             return generation.getText();
        //         });
        //             // .content()
        //             // // Catch any streaming exceptions (like bad authentication or bad payload mappings)
        //             // .onErrorResume(throwable -> {
        //             //     log.error("Streaming error intercepted: ", throwable);
        //             //     return Flux.just("[Backend Stream Error: " + throwable.getMessage() + "]");
        //             // });
                    
        // } catch (Exception e) {
        //     // Catch any initialization exceptions (like missing configuration properties)
        //     log.error("Client prompt build error: ", e);
        //     return Flux.just("[Backend Init Error: " + e.getMessage() + "]");
        // }
    }

}


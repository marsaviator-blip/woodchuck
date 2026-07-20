package org.woodchuck.zChecker.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3002") // Default Vite dev port
public class ChatController {

    private final ChatClient chatClient;

    //Injecting the auto-configured builder provided by Spring AI

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
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


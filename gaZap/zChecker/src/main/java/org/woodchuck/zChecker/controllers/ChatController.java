package org.woodchuck.zChecker.controllers;

//import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173") // Default Vite dev port
public class ChatController {

    // private final ChatClient chatClient;

    // Injecting the auto-configured builder provided by Spring AI

    // public ChatController(ChatClient.Builder chatClientBuilder) {
    //     this.chatClient = chatClientBuilder.build();
    // }
    // @GetMapping(value = "/stream", produces = "text/event-stream")
    // public Flux<String> streamChat(@RequestParam String message) {
    //     return this.chatClient.prompt()
    //             .user(message)
    //             .stream() // Enables token-by-token streaming
    //             .content();
    // }
}


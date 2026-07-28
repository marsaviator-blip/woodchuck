package org.woodchuck.zChecker.controllers;

import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Flux;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. CATCH PERMANENT MODEL FAILURES (Bad requests, token length, billing)
    @ExceptionHandler(NonTransientAiException.class)
    public Flux<ServerSentEvent<String>> handleNonTransientAiException(NonTransientAiException ex) {
        String msg = "⚠️ AI Model Configuration Error: Your prompt or token limit failed.";
        
        if (ex.getMessage() != null && ex.getMessage().contains("context_length_exceeded")) {
            msg = "⚠️ Chat Context Window Exceeded! Please clear history to continue.";
        } else if (ex.getMessage() != null && ex.getMessage().contains("content_policy")) {
            msg = "⚠️ Content Policy Block: The message violates provider safety guards.";
        }
        
        return createErrorEventStream(msg);
    }

    // 2. CATCH TEMPORARY Access FAILURES (Rate limits, provider offline)
    @ExceptionHandler(TransientAiException.class)
    public Flux<ServerSentEvent<String>> handleTransientAiException(TransientAiException ex) {
        String msg = "⏳ AI Service temporarily unavailable. Retrying connection shortly...";
        
        if (ex.getMessage() != null && ex.getMessage().contains("429")) {
            msg = "⏳ Rate limit exceeded. Too many messages hit the LLM. Please slow down.";
        }
        
        return createErrorEventStream(msg);
    }

    // Helper method to pipe the message clean to your Angular/Vue stream reader
    private Flux<ServerSentEvent<String>> createErrorEventStream(String message) {
        return Flux.just(ServerSentEvent.<String>builder()
                .event("error")
                .data(message)
                .build());
    }
}

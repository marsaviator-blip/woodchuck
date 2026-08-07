package org.woodchuck.zChecker.services;

import org.woodchuck.zChecker.dtos.ChatMessageRequest; // request and response are currenly the same, but they can diverge in the future
import org.woodchuck.zChecker.dtos.UserMessageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WorkspaceSyncService {

    private final RestClient restClient;

    public WorkspaceSyncService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ChatMessageRequest fetchMessageDetails(Long messageId) {
        return this.restClient.get()
            .uri("/api/messages/{id}", messageId)
            .retrieve()
            .body(ChatMessageRequest.class); // Automatically deserializes JSON to your DTO
    }

    public void sendDataIngestPayload(UserMessageDTO payload) {
        this.restClient.post()
            .uri("/api/ingest")
            .body(payload)
            .retrieve()
            .toBodilessEntity(); // Executes a POST without expecting a return body
    }
}

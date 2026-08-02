package org.woodchuck.zChecker.controllers;
package com.example.controller;

import com.example.dto.KnowledgeMorphismGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
@RequestMapping("/api/workspace")
@CrossOrigin(origins = "*") // Allows clean local testing from your UI apps
public class WorkspaceSyncController {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceSyncController.class);
    private final RestClient pgStoreClient;

    // Inject RestClient configured for your backend 'pgstore' microservice
    public WorkspaceSyncController() {
        this.pgStoreClient = RestClient.builder()
                .baseUrl("http://localhost:8087") // URL pointing to the downstream pgstore microservice
                .build();
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> synchronizeMorphismGroup(
            @RequestBody KnowledgeMorphismGroup morphismGroup) {
        
        log.info("Received OKF Morphism Group for sync. GroupId: {}, Category Domain: {}", 
                morphismGroup.groupId(), morphismGroup.category());

        try {
            // Forward the structural schema envelope downstream to the backend pgstore
            ResponseEntity<Void> response = pgStoreClient.post()
                    .uri("/api/store/commit")
                    .body(morphismGroup)
                    .retrieve()
                    .toBodilessEntity();

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Morphism dataset successfully committed to pgstore near-term vault."
                ));
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(Map.of(
                    "status", "STORE_ERROR",
                    "message", "Downstream pgstore rejected the dataset payload structure."
                ));
            }

        } catch (Exception e) {
            log.error("Failed to route workspace group transaction to pgstore", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "GATEWAY_FAILURE",
                "message", "Internal processing connectivity failure: " + e.getMessage()
            ));
        }
    }
}


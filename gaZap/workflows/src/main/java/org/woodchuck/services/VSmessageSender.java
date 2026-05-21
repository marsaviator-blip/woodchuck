package org.woodchuck.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.woodchuck.dtos.BibliographyResponse;

import java.util.concurrent.CompletableFuture;

@Service
public class VSmessageSender {

    private static final Logger logger = LoggerFactory.getLogger(VSmessageSender.class);
    private static final String TOPIC_NAME = "woodchuck";
    private static final String BIBTEX_TOPIC_NAME = "woodchuck-bibtex";
    private static final String GROUP_ID = "vsProducerGroup";   
    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // Adjust as needed
    private static final int POLL_TIMEOUT_MS = 1000; // Adjust as needed
    private static final int MAX_POLL_RECORDS = 10; // Adjust as needed
 
    private KafkaTemplate<String, String> kafkaTemplate;
    private KafkaTemplate<String, BibliographyResponse> kafkaBibtexTemplate;
    
    public VSmessageSender(KafkaTemplate<String, String> kafkaTemplate, KafkaTemplate<String, BibliographyResponse> kafkaBibtexTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaBibtexTemplate = kafkaBibtexTemplate;
    }

    public void sendMessage(String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_NAME, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Sent message=[" + message + 
                "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" + 
                    message + "] due to : " + ex.getMessage());
            }
        });
    }

    public void sendBibtexMessage(BibliographyResponse bibtex) {
        CompletableFuture<SendResult<String, BibliographyResponse>> future = kafkaBibtexTemplate.send(BIBTEX_TOPIC_NAME, bibtex);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Sent message=[" + bibtex + 
                "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" + 
                    bibtex + "] due to : " + ex.getMessage());
            }
        });
    }
}

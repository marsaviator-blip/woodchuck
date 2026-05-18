package org.woodchuck.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class VSmessageSender {

    private static final Logger logger = LoggerFactory.getLogger(VSmessageSender.class);
    private static final String TOPIC_NAME = "woodchuck";
    private static final String GROUP_ID = "vsProducerGroup";   
    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // Adjust as needed
    private static final int POLL_TIMEOUT_MS = 1000; // Adjust as needed
    private static final int MAX_POLL_RECORDS = 10; // Adjust as needed
 
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public VSmessageSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
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
}

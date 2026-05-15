package org.woodchuck.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class VSmessageReceiver {
    
    private static final Logger logger = LoggerFactory.getLogger(VSmessageReceiver.class);
    private static final String TOPIC_NAME = "woodchuck";
    private static final String GROUP_ID = "vsProducerGroup";   
    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // Adjust as needed
    private static final int POLL_TIMEOUT_MS = 1000; // Adjust as needed
    private static final int MAX_POLL_RECORDS = 10; // Adjust as needed
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public VSmessageReceiver() {

    }

    @KafkaListener(topics = "woodchuck", groupId = "vsProducerGroup")
    public void receiveMessage(String message) {
        logger.info("Received message: {}", message);
        // Process the message as needed
    }
}

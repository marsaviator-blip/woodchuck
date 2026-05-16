package org.woodchuck.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class VSmessageReceiver {
    
    private static final Logger logger = LoggerFactory.getLogger(VSmessageReceiver.class);
    private static final String TOPIC_NAME = "woodchuck";

    @KafkaListener(topics = TOPIC_NAME)
    public void receiveMessage(String message) {
        logger.info("Received message: {}", message);
        // Process the message as needed
    }
}

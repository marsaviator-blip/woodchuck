package org.woodchuck.configs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer; // New Import

import java.util.HashMap;
import java.util.Map;

import org.woodchuck.dtos.BibliographyResponse;


@Configuration
public class KafkaBibtexConfig {

    // Make it final to ensure immutability
    private final KafkaConsumerConfig globalConfig;

    // Single constructor: Spring injects KafkaConsumerConfig automatically
    public KafkaBibtexConfig(KafkaConsumerConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    // 1. Build a localized properties map just for this factory
    private Map<String, Object> bibtexConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, globalConfig.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, globalConfig.getConsumer().getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Do NOT put VALUE_DESERIALIZER_CLASS_CONFIG here; pass the object instead
        return props;
    }

    // 2. Create the specific factory using the explicit JSON deserializer object
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BibliographyResponse> bibtexKafkaListenerContainerFactory() {
        JacksonJsonDeserializer<BibliographyResponse> jsonDeserializer = new JacksonJsonDeserializer<>(BibliographyResponse.class);
        jsonDeserializer.addTrustedPackages("*"); 

        DefaultKafkaConsumerFactory<String, BibliographyResponse> consumerFactory = 
                new DefaultKafkaConsumerFactory<>(
                        bibtexConsumerConfigs(), 
                        new StringDeserializer(), 
                        jsonDeserializer // Strictly bound only to this factory
                );

        ConcurrentKafkaListenerContainerFactory<String, BibliographyResponse> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}

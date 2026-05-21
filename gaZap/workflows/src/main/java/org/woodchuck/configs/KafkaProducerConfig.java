package org.woodchuck.configs;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import org.woodchuck.dtos.BibliographyResponse;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // Adjust as needed

    @Bean
    ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
          BOOTSTRAP_SERVERS);
        configProps.put(
          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
          StringSerializer.class);
        configProps.put(
          ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
          StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public ProducerFactory<String, BibliographyResponse> producerBibtexFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Define your Kafka broker location
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Set the standard serializers
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        // Optional: If you chose Method A (Duplicate Records), define the outbound type mapping token here:
        configProps.put(JacksonJsonSerializer.TYPE_MAPPINGS, "bibliography:org.woodchuck.dtos.BibliographyResponse");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, BibliographyResponse> kafkaBibtexTemplate() {
        return new KafkaTemplate<>(producerBibtexFactory());
    }
}

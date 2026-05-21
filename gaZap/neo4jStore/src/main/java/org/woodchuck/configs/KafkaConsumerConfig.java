package org.woodchuck.configs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import org.woodchuck.dtos.BibliographyResponse;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class KafkaConsumerConfig {

    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Bean
    public ConsumerFactory<String, BibliographyResponse> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "bibliography-group");

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class.getName());

        // 1. 🟢 Map the producer's string alias to your consumer's actual local class path
        config.put(JacksonJsonDeserializer.TYPE_MAPPINGS, 
            "bibliography:org.woodchuck.dtos.BibliographyResponse"
        );
        
        // 2. 🟢 Add the string alias itself to the trusted packages list!
        config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "org.woodchuck.dtos,bibliography");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BibliographyResponse> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BibliographyResponse> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

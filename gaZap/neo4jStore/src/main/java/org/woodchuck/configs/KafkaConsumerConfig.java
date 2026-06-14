package org.woodchuck.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaConsumerConfig {

    private String bootstrapServers; // Maps to spring.kafka.bootstrap-servers
    private Consumer consumer = new Consumer(); // Maps to spring.kafka.consumer.*

    // Nested class matches the "consumer:" block in YAML perfectly
    public static class Consumer {
        private String groupId;

        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
    }

    // Getters and Setters for the main properties
    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }

    public Consumer getConsumer() { return consumer; }
    public void setConsumer(Consumer consumer) { this.consumer = consumer; }
}

# AI Coding Instructions for this repo

- This is a Spring Boot Kafka consumer application. The primary entrypoint is `src/main/java/org/woodchuck/vsbus/VsbusConsumerApplication.java`.
- The app is built with Maven and Spring Boot 4.0.6, using Java 21. Use `./mvnw clean test` and `./mvnw spring-boot:run` as the standard workflows.
- Runtime configuration is sparse: `src/main/resources/application.yaml` only defines `spring.application.name: vsbus`. Kafka bootstrap servers are expected from `spring.kafka.bootstrap-servers` in runtime configuration or environment overrides.

- Key components:
  - `src/main/java/org/woodchuck/configs/KafkaConsumerConfig.java` defines the consumer factory and listener container.
  - `src/main/java/org/woodchuck/configs/KafkaTopicConfig.java` defines a `NewTopic` bean for topic `woodchuck`.
  - `src/main/java/org/woodchuck/services/VSmessageReceiver.java` consumes messages with `@KafkaListener(topics = "woodchuck", groupId = "vsProducerGroup")`.

- Pay attention to the current group-id mismatch:
  - `KafkaConsumerConfig` sets `GROUP_ID = "vsConsumerGroup"`
  - `VSmessageReceiver` listener uses `groupId = "vsProducerGroup"`
  Confirm the intended consumer group before refactoring.

- Note the Kafka producer side is not currently configured in this codebase. `VSmessageReceiver` autowires `KafkaTemplate<String, String>`, but producer properties are absent; do not assume a valid producer bean exists without adding producer configuration.

- Tests are minimal. `src/test/java/org/woodchuck/vsbus/VsbusConsumerApplicationTests.java` only verifies context startup, so preserve application bootstrap behavior when changing wiring.

- Keep package boundaries intact: `org.woodchuck.configs` for Kafka wiring, `org.woodchuck.services` for message processing, `org.woodchuck.vsbus` for the application entrypoint.

- Consult `HELP.md` for Maven parent inheritance notes and Spring Boot/Kafka reference links if build or plugin behavior needs validation.

If any section is unclear or missing, ask for feedback before making broader structural changes.
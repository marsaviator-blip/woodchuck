package org.woodchuck.configs;  

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MinioStorageConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * Initializes and registers the MinioClient into the Spring IoC Context
     * so it can be cleanly injected into your SessionProcessingPipeline.
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * Lifecycle Listener intercepting app-readiness to verify and auto-provision
     * the knowledge storage vault bucket before the user starts executing sessions.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void autoProvisionKnowledgeBucket() {
        MinioClient client = minioClient();
        log.info("Validating presence of target knowledge-vault object store bucket...");

        try {
            boolean bucketExists = client.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!bucketExists) {
                log.warn("Target bucket '{}' not found. Executing automatic provisioning...", bucketName);
                
                client.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
                );
                
                log.info("Bucket '{}' successfully provisioned and secured.", bucketName);
            } else {
                log.info("Bucket '{}' verified active. Storage lifecycle operational.", bucketName);
            }
        } catch (Exception e) {
            log.error("CRITICAL ERROR: Failed to communicate with or provision MinIO storage infrastructure!", e);
            // Optional: System.exit(1); in production if your core system absolutely depends on blob storage presence
        }
    }
}


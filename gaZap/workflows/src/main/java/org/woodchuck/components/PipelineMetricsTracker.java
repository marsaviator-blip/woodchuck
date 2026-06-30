package org.woodchuck.components;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Tracks custom metrics for the unstructured thematic analysis engine.
 * These metrics are exposed via Spring Boot Actuator at the /actuator/prometheus endpoint.
 */
@Component
public class PipelineMetricsTracker {

    private final Counter skippedUrlsCounter;
    private final Counter aiTransientErrorsCounter;

    public PipelineMetricsTracker(MeterRegistry registry) {
        // Tracks URLs dropped permanently due to 403 Forbidden or 404 Not Found errors
        this.skippedUrlsCounter = Counter.builder("discovery.engine.urls.skipped")
                .description("Total number of discovered web URLs skipped due to permanent HTTP scraping blocks")
                .tag("component", "DoclingReader")
                .register(registry);

        // Tracks transient issues like network timeouts or Gemini API 429 Rate Limits
        this.aiTransientErrorsCounter = Counter.builder("discovery.engine.ai.transient.retries")
                .description("Total count of transient recoverable exceptions triggered during AI processing")
                .tag("component", "GeminiClient")
                .register(registry);
    }

    /**
     * Increments the skipped URL counter when a permanent 4xx error occurs.
     */
    public void incrementSkippedUrls() {
        this.skippedUrlsCounter.increment();
    }

    /**
     * Increments the transient error counter to record an upcoming Temporal retry.
     */
    public void incrementTransientErrors() {
        this.aiTransientErrorsCounter.increment();
    }
}


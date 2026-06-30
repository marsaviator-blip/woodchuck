package org.woodchuck.temporal.exceptions;

/**
 * Thrown during pipeline execution when a permanent, unrecoverable failure occurs 
 * (e.g., all URLs failed to parse, invalid API credentials, or broken configuration states).
 * 
 * Temporal is configured to intercept this exception via the DoNotRetry policy,
 * causing the workflow to fail fast rather than attempting wasteful retries.
 */
public class NonRetryableExtractionException extends RuntimeException {

    /**
     * Constructs a new exception with a specific detail message.
     *
     * @param message the contextual error message describing the permanent failure
     */
    public NonRetryableExtractionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with a detail message and the underlying root cause.
     *
     * @param message the contextual error message describing what failed
     * @param cause the root exception causing the structural failure
     */
    public NonRetryableExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}


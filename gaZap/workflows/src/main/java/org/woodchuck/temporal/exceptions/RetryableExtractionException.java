package org.woodchuck.temporal.exceptions;

/**
 * Thrown during pipeline execution when a transient, recoverable failure occurs 
 * (e.g., network timeouts, 503 Server Errors, or LLM API rate limits).
 * 
 * Temporal is configured to intercept this exception and execute an automatic retry 
 * sequence using exponential backoff.
 */
public class RetryableExtractionException extends RuntimeException {

    /**
     * Constructs a new exception with a specific detail message.
     *
     * @param message the contextual error message describing what failed
     */
    public RetryableExtractionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with a detail message and the underlying root cause.
     *
     * @param message the contextual error message describing what failed
     * @param cause the root exception (e.g., HttpServerErrorException or ResourceAccessException)
     */
    public RetryableExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}


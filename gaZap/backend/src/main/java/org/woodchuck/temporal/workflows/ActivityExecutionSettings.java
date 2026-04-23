package org.woodchuck.temporal.workflows;

import java.io.Serializable;

public class ActivityExecutionSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private int timeoutSeconds;
    private int initialIntervalSeconds;
    private double backoffCoefficient;
    private int maximumIntervalSeconds;
    private int maximumAttempts;

    public ActivityExecutionSettings() {
    }

    public ActivityExecutionSettings(
        int timeoutSeconds,
        int initialIntervalSeconds,
        double backoffCoefficient,
        int maximumIntervalSeconds,
        int maximumAttempts
    ) {
        this.timeoutSeconds = timeoutSeconds;
        this.initialIntervalSeconds = initialIntervalSeconds;
        this.backoffCoefficient = backoffCoefficient;
        this.maximumIntervalSeconds = maximumIntervalSeconds;
        this.maximumAttempts = maximumAttempts;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getInitialIntervalSeconds() {
        return initialIntervalSeconds;
    }

    public void setInitialIntervalSeconds(int initialIntervalSeconds) {
        this.initialIntervalSeconds = initialIntervalSeconds;
    }

    public double getBackoffCoefficient() {
        return backoffCoefficient;
    }

    public void setBackoffCoefficient(double backoffCoefficient) {
        this.backoffCoefficient = backoffCoefficient;
    }

    public int getMaximumIntervalSeconds() {
        return maximumIntervalSeconds;
    }

    public void setMaximumIntervalSeconds(int maximumIntervalSeconds) {
        this.maximumIntervalSeconds = maximumIntervalSeconds;
    }

    public int getMaximumAttempts() {
        return maximumAttempts;
    }

    public void setMaximumAttempts(int maximumAttempts) {
        this.maximumAttempts = maximumAttempts;
    }
}
package org.woodchuck.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.temporal.bio")
public class BioTemporalProperties {
    private ActivityPolicy search = new ActivityPolicy();
    private ActivityPolicy data = new ActivityPolicy();

    public ActivityPolicy getSearch() {
        return search;
    }

    public void setSearch(ActivityPolicy search) {
        this.search = search;
    }

    public ActivityPolicy getData() {
        return data;
    }

    public void setData(ActivityPolicy data) {
        this.data = data;
    }

    public static class ActivityPolicy {
        private int timeoutSeconds = 10;
        private int initialIntervalSeconds = 1;
        private double backoffCoefficient = 2.0;
        private int maximumIntervalSeconds = 10;
        private int maximumAttempts = 3;

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
}
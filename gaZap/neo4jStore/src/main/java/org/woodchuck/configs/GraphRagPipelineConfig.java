package org.woodchuck.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "graphrag.pipeline")
public class GraphRagPipelineConfig {

    public enum Strategy { LIGHT, DEEP }

    private Strategy strategy = Strategy.DEEP; // Default value if omitted in YAML
    private boolean fallbackToLightOnFailure = true;

    // Getters and Setters
    public Strategy getStrategy() { return strategy; }
    public void setStrategy(Strategy strategy) { this.strategy = strategy; }
    
    public boolean isFallbackToLightOnFailure() { return fallbackToLightOnFailure; }
    public void setFallbackToLightOnFailure(boolean fallbackToLightOnFailure) { 
        this.fallbackToLightOnFailure = fallbackToLightOnFailure; 
    }
}

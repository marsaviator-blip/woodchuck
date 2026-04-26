package org.woodchuck.temporal.workflows.specs;

import org.springframework.stereotype.Component;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;

@Component
public class MPSpec {
    private boolean shouldWorkflowRemainAlive = false;
    private boolean testSingleMPId = false;
    private boolean shouldCallNeo4j = false;
    private boolean shouldGetStructure = false;
    private boolean shouldGetProvenance = false;
    private boolean shouldGetDOI = false;
    private boolean shouldGetCIF = false;

    private ActivityExecutionSettings settings = new ActivityExecutionSettings();

    private String elementId;
    
    private boolean deprecated;
    private int per_page;
    private int skip;
    private int limit;
    private String license;

    public boolean isShouldWorkflowRemainAlive() {
        return shouldWorkflowRemainAlive;
    }
    public void setShouldWorkflowRemainAlive(boolean shouldWorkflowRemainAlive) {
        this.shouldWorkflowRemainAlive = shouldWorkflowRemainAlive;
    }
    public boolean isTestSingleMPId() {
        return testSingleMPId;
    }
    public void setTestSingleMPId(boolean testSingleMPId) {
        this.testSingleMPId = testSingleMPId;
    }
    public boolean isShouldCallNeo4j() {
        return shouldCallNeo4j;
    }
    public void setShouldCallNeo4j(boolean shouldCallNeo4j) {
        this.shouldCallNeo4j = shouldCallNeo4j;
    }
    public boolean isShouldGetStructure() {
        return shouldGetStructure;
    }
    public void setShouldGetStructure(boolean shouldGetStructure) {
        this.shouldGetStructure = shouldGetStructure;
    }
    public boolean isShouldGetProvenance() {
        return shouldGetProvenance;
    }
    public void setShouldGetProvenance(boolean shouldGetProvenance) {
        this.shouldGetProvenance = shouldGetProvenance;
    }   
    public boolean isShouldGetDOI() {
        return shouldGetDOI;
    }
    public void setShouldGetDOI(boolean shouldGetDOI) {
        this.shouldGetDOI = shouldGetDOI;
    }
    public boolean isShouldGetCIF() {
        return shouldGetCIF;
    }   
    public void setShouldGetCIF(boolean shouldGetCIF) {
        this.shouldGetCIF = shouldGetCIF;
    }   
    public ActivityExecutionSettings getSettings() {
        return settings;
    }
    public void setSettings(ActivityExecutionSettings settings) {
        this.settings = settings;
    }   
    public String getElementId() {
        return elementId;
    }
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }
    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }   
}

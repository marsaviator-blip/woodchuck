package org.woodchuck.temporal.workflows.specs;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Specification for Bio workflow execution")
public class BioWorkflowRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Operation {
        SEARCH,
        GET_DATA
    }

    @Schema(description = "The operation to perform: SEARCH or GET_DATA")   
    private Operation operation;

    @Schema(description = "The search query to execute when operation is SEARCH")
    private String query;
    private List<String> entries;
    @Schema(hidden = true)
    private ActivityExecutionSettings settings= new ActivityExecutionSettings();

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public ActivityExecutionSettings getSettings() {
        return settings;
    }

    public void setSettings(ActivityExecutionSettings settings) {
        this.settings = settings;
    }
}
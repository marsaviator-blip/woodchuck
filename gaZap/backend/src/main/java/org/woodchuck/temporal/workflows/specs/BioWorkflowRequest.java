package org.woodchuck.temporal.workflows.specs;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;

@Component
public class BioWorkflowRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Operation {
        SEARCH,
        GET_DATA
    }

    private Operation operation;
    private String query;
    private List<String> entries;
    private ActivityExecutionSettings settings;

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
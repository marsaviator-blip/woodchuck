package org.woodchuck.temporal.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PdfIngestionWorkflow {

    @WorkflowMethod
    void execute(String url);
}

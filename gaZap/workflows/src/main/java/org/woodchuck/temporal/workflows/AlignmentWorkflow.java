package org.woodchuck.temporal.workflows;

import java.util.List;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AlignmentWorkflow {

    @WorkflowMethod
    String execute(List<String> entries);
}

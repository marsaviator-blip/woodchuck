package org.woodchuck.temporal.workflows;

import org.woodchuck.dtos.CrossrefXmlResponse;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CrossrefWorkflow {

    @SignalMethod
    void startUp(String doi);

    @WorkflowMethod
    CrossrefXmlResponse cross(String doi);
}

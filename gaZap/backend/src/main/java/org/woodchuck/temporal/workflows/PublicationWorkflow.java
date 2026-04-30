package org.woodchuck.temporal.workflows;

import io.temporal.workflow.WorkflowInterface;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PublicationWorkflow {

   @WorkflowMethod
    String retrieve(String doi);
}

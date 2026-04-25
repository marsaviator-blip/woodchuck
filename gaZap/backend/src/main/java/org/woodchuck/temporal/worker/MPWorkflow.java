package org.woodchuck.temporal.worker;

import org.springframework.core.annotation.Order;
import org.woodchuck.dtos.MaterialStructureParams;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.woodchuck.temporal.workflows.MPSpec;

@WorkflowInterface
public interface MPWorkflow {
   
    @WorkflowMethod
    void processMP(MPSpec spec);
}



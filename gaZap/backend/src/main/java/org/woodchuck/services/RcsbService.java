package org.woodchuck.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.woodchuck.config.BioTemporalProperties;
import org.woodchuck.dtos.SearchQueryParams;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.temporal.workflows.BioWorkflow;
import org.woodchuck.temporal.workflows.BioWorkflowRequest;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;

@Service
public class RcsbService {
    private static final String BIO_TASK_QUEUE = "BioTaskQueue";

    @Autowired
    private WorkflowClient workflowClient;

    @Autowired
    private BioTemporalProperties bioTemporalProperties;
    
    public List<String> search(SearchQueryParams params) {
        BioWorkflow workflow = newWorkflow();
        BioWorkflowRequest request = new BioWorkflowRequest();
        request.setOperation(BioWorkflowRequest.Operation.SEARCH);
        request.setQuery(params.getQuery());
        request.setSettings(toSettings(bioTemporalProperties.getSearch()));
        return workflow.execute(request);
    }

    public List<String> getData(List<String> entries) {
        BioWorkflow workflow = newWorkflow();
        BioWorkflowRequest request = new BioWorkflowRequest();
        request.setOperation(BioWorkflowRequest.Operation.GET_DATA);
        request.setEntries(entries);
        request.setSettings(toSettings(bioTemporalProperties.getData()));
        return workflow.execute(request);
    }

    private BioWorkflow newWorkflow() {
        return workflowClient.newWorkflowStub(BioWorkflow.class,
            WorkflowOptions.newBuilder().setTaskQueue(BIO_TASK_QUEUE).build());
    }

    private ActivityExecutionSettings toSettings(BioTemporalProperties.ActivityPolicy policy) {
        return new ActivityExecutionSettings(
            policy.getTimeoutSeconds(),
            policy.getInitialIntervalSeconds(),
            policy.getBackoffCoefficient(),
            policy.getMaximumIntervalSeconds(),
            policy.getMaximumAttempts());
    }
}

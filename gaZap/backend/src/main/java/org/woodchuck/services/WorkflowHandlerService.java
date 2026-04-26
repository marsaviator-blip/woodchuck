package org.woodchuck.services;

import org.springframework.stereotype.Service;
import org.woodchuck.temporal.services.ParentWorkflowServiceImpl;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.temporal.workflows.specs.BioWorkflowRequest;
import org.woodchuck.temporal.workflows.specs.MPSpec;

@Service
public class WorkflowHandlerService {

    private final ParentWorkflowServiceImpl pwService;
    private final BioWorkflowRequest bioRequest;
    private final MPSpec mpSpec;

    //Constructor injection
    public WorkflowHandlerService(ParentWorkflowServiceImpl pwService,
                                        BioWorkflowRequest bioRequest,
                                        MPSpec mpSpec       
    ) {
        this.bioRequest = bioRequest;
        this.mpSpec = mpSpec;
        this.pwService = pwService;
    }

    // need to pass input from controller to this service, and then to the workflow
    public void handleWorkflow() {
        System.out.println("WorkflowHandlerService handling Temporal workflows.");
        // You can customize the BioWorkflowRequest and MPSpec as needed

        // pwService.startBioWorkflow();
        pwService.startMPWorkflow();
    }
}

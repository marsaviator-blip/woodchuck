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

    public void handleWorkflow() {
        System.out.println("ClientRunnerParentWorkflow scheduling startup Temporal workflows.");
        // You can customize the BioWorkflowRequest and MPSpec as needed
        BioWorkflowRequest bioRequest = new BioWorkflowRequest();
        bioRequest.setOperation(BioWorkflowRequest.Operation.SEARCH);
        bioRequest.setQuery("example query");
        bioRequest.setSettings(new ActivityExecutionSettings(30, 5, 2.0, 60, 3));

        MPSpec mpSpec = new MPSpec();
//        mpSpec.setParameter("example parameter");

        pwService.startBioWorkflow(bioRequest);
        pwService.startMPWorkflow(mpSpec);
    }
}

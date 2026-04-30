package org.woodchuck.components;

import org.springframework.stereotype.Component;
import org.woodchuck.temporal.activities.MPActivitiesImpl;
import org.woodchuck.temporal.services.MPWorkflowImpl;
import org.woodchuck.temporal.services.MPWorkflowService;
import org.woodchuck.temporal.workflows.MPWorkflow;
import org.woodchuck.temporal.workflows.specs.MPSpec;
import org.woodchuck.dtos.SearchQueryParams;
import org.woodchuck.temporal.services.BioWorkflowService;
import org.woodchuck.temporal.workflows.BioWorkflow;
import org.woodchuck.temporal.workflows.CrossrefWorkflow;
import org.woodchuck.temporal.services.CrossrefWorkflowService;
import org.woodchuck.temporal.workflows.specs.BioWorkflowRequest;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.util.UUID;

import org.checkerframework.checker.units.qual.m;
import org.springframework.boot.CommandLineRunner;

//@Component
public class ClientRunnerParentWorkflow {//} CommandLineRunner {

    // private final WorkflowHandlerService wfhService;

    // //Constructor injection
    // public ClientRunnerParentWorkflow(WorkflowHandlerService wfhService) {
    //     this.wfhService = wfhService;
    // }
    
    private final MPWorkflowService mpService;
    private final MPSpec mpSpec;
    private final BioWorkflowService bioService;
    private final BioWorkflowRequest bioRequest;
    private final CrossrefWorkflowService crossrefService;
    private WorkflowClient workflowClient;


    public ClientRunnerParentWorkflow(MPWorkflowService mpService, MPSpec mpSpec, BioWorkflowService bioService, BioWorkflowRequest bioRequest, CrossrefWorkflowService crossrefService, WorkflowClient wfClient) {
        this.mpService = mpService;
        this.mpSpec = mpSpec;
        this.bioService = bioService;
        this.bioRequest = bioRequest;
        this.crossrefService = crossrefService;
        this.workflowClient = wfClient;

        this.mpSpec.setElementId("CaHPO4");
        this.mpService.createMPWorkflow(mpSpec);
        SearchQueryParams params = new SearchQueryParams("pyrophosphatase"  , "entry");
        this.bioRequest.setQuery(params.getQuery());
        this.bioRequest.setOperation(BioWorkflowRequest.Operation.SEARCH);
        this.bioService.createBioWorkflow(bioRequest);
        this.crossrefService.createCrossrefWorkflow("10.1107/S0108767317098695");
        this.bioService.createPublicationWorkflow("10.1111/1751-7915.70355");
        //String cwfId = execution.getWorkflowId();
        // var wfId = mpService.createMPWorkflow(mpSpec);
        // MPWorkflow mpWorkflow = this.workflowClient.newWorkflowStub(
        //                 MPWorkflow.class, wfId);
        // mpWorkflow.processMP(this.mpSpec);
    }
    //private static final String MP_QUEUE = "MP_QUEUE";
    //private final WorkflowHandlerService wfhService;
//     private final WorkflowClient workflowClient;
//     private final MPActivitiesImpl mpActivities;
//     //Constructor injection
//     public ClientRunnerParentWorkflow( 
//                                             WorkflowClient workflowClient, 
//                                             MPActivitiesImpl mpActivities) {
// //        this.wfhService = wfhService;
//         this.workflowClient = workflowClient;
//         this.mpActivities = mpActivities;
//         WorkerFactory workerFactory = WorkerFactory.newInstance(workflowClient);
//         Worker worker = workerFactory.newWorker(MP_QUEUE);
//         worker.registerWorkflowImplementationTypes(MPWorkflowImpl.class);

//         worker.registerActivitiesImplementations(mpActivities);
//         workerFactory.start(); 
//      }

    // public void fetchRcsbData(String query) {
    //     System.out.println("Fetching RCSB data for query: " + query);
    //     SearchQueryParams params = new SearchQueryParams(query, "entry");
    //     List<String> results = rcsbService.search(params);
    //     System.out.println("RCSB search results for query '" + query + "': " + results);
    //     List<String> data = rcsbService.getData(results);
    //     System.out.println("RCSB data for query '" + query + "': " + data.size() + " entries");
    // }

   // @Override
    public void run(String... args) {

        System.out.println("ClientRunnerParentWorkflow run method scheduling startup Temporal workflow.");
        try {
            Thread.sleep(1000); // Sleep for 1 second to allow the worker to start up and be ready to receive workflow tasks
        } catch (InterruptedException e) {
            e.printStackTrace();   
        } 
        String wfId = mpService.getWorkflowId();
        MPWorkflow mpWorkflow = this.workflowClient.newWorkflowStub(
                        MPWorkflow.class, wfId);
        mpSpec.setElementId("CaHPO4");
        mpWorkflow.startUp(this.mpSpec);

        BioWorkflow bioWorkflow = this.workflowClient.newWorkflowStub(
                        BioWorkflow.class, bioService.getWorkflowId());
        bioWorkflow.startUp(this.bioRequest);

        // CrossrefWorkflow crossrefWorkflow = this.workflowClient.newWorkflowStub(
        //                 CrossrefWorkflow.class, crossrefService.getWorkflowId());
        // crossrefWorkflow.startUp("10.1107/S0108767317098695");
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            e.printStackTrace();   
        } 
        mpWorkflow.resetFlags();
        mpSpec.setElementId("P2O5");
        mpWorkflow.startUp(this.mpSpec);

        System.out.println("ClientRunnerParentWorkflow run method completed scheduling Temporal workflow.");
        mpWorkflow.complete();
    }

    // public static void main(String[] args) {
    // This main method is not needed for Spring Boot applications, as the
    // application
    // will be started by Spring Boot's auto-configuration. However, you can use it
    // for testing purposes if needed.
    // }

}

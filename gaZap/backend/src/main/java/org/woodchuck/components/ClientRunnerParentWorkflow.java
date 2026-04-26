package org.woodchuck.components;

import org.springframework.stereotype.Component;
import org.woodchuck.temporal.activities.MPActivitiesImpl;
import org.woodchuck.temporal.services.MPWorkflowImpl;
import org.woodchuck.temporal.services.MPWorkflowService;
import org.woodchuck.temporal.workflows.MPWorkflow;
import org.woodchuck.temporal.workflows.specs.MPSpec;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import org.springframework.boot.CommandLineRunner;

@Component
public class ClientRunnerParentWorkflow implements CommandLineRunner {

    // private final WorkflowHandlerService wfhService;

    // //Constructor injection
    // public ClientRunnerParentWorkflow(WorkflowHandlerService wfhService) {
    //     this.wfhService = wfhService;
    // }
    
    private final MPWorkflowService mpService;
    //private final MPWorkflowImpl mpWorkflowImpl;  
    private final MPSpec mpSpec;
    private WorkflowClient workflowClient;


    public ClientRunnerParentWorkflow(MPWorkflowService mpService, MPSpec mpSpec, WorkflowClient wfClient) {
        this.mpService = mpService;
        this.workflowClient = wfClient;
        this.mpSpec = mpSpec;
        this.mpSpec.setElementId("CaHPO4");
        mpService.createMPWorkflow(mpSpec);
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

    @Override
    public void run(String... args) {

        System.out.println("ClientRunnerParentWorkflow run method scheduling startup Temporal workflow.");
//        wfhService.handleWorkflow("CaHPO4");
        String wfId = mpService.getWorkflowId();
        MPWorkflow mpWorkflow = this.workflowClient.newWorkflowStub(
                        MPWorkflow.class, wfId);
        mpWorkflow.processMP(this.mpSpec);

    }

    // public static void main(String[] args) {
    // This main method is not needed for Spring Boot applications, as the
    // application
    // will be started by Spring Boot's auto-configuration. However, you can use it
    // for testing purposes if needed.
    // }

}

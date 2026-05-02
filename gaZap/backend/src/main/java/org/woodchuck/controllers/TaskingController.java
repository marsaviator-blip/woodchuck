package org.woodchuck.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.woodchuck.dtos.SearchQueryParams;
import org.woodchuck.temporal.workflows.BioWorkflow;
import org.woodchuck.temporal.workflows.MPWorkflow;
import org.woodchuck.temporal.workflows.PublicationWorkflow;
import org.woodchuck.temporal.workflows.CitationWorkflow;
import org.woodchuck.temporal.workflows.CrossrefWorkflow;
import org.woodchuck.temporal.workflows.specs.BioWorkflowRequest;
import org.woodchuck.temporal.workflows.specs.MPSpec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;

@RestController
@RequestMapping("/gaZap/workflows") // Base path for all endpoints in this class
public class TaskingController {

    private final WorkflowClient workflowClient;

    public TaskingController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @GetMapping
    @Operation(summary = "Start an MP workflow", 
                description = "Starts a new workflow based on the provided input.") 
    public String startMPWorkflow(@Parameter(description = "Input data for the workflow", example="CaHPO4", required = true) @RequestParam(required = true) String elementId) {
        var uuid = UUID.randomUUID();
        MPSpec mpSpec = new MPSpec();
        mpSpec.setElementId(elementId);
        WorkflowOptions options = WorkflowOptions.newBuilder()
            .setWorkflowId(uuid.toString())
            .setTaskQueue("MP_QUEUE")
            .build();
        MPWorkflow workflow = workflowClient.newWorkflowStub(MPWorkflow.class, options);
        WorkflowClient.start(workflow::processMP, mpSpec);
        return "MP Workflow started successfully with element ID: " + elementId;
    }

    @GetMapping(value = "/bio")
    @Operation(summary = "Start an BIO workflow", 
                description = "Starts a new workflow based on the provided input.") 
    public String startBIOWorkflow(@Parameter(description = "Input data for the workflow", example="pyrophosphatase", required = true) @RequestParam(required = true) String queryValue) {
        var uuid = UUID.randomUUID();
        SearchQueryParams params = new SearchQueryParams(queryValue, "entry");
        System.out.println("Received query: " + queryValue+" "+params.getQuery()+" "+params.getReturnType());
        BioWorkflowRequest bioSpec = new BioWorkflowRequest();
        bioSpec.setQuery(params.getQuery());
        bioSpec.setOperation(BioWorkflowRequest.Operation.SEARCH);
        var wf = workflowClient.newWorkflowStub(
            BioWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue("BioTaskQueue")
                .setWorkflowId(uuid.toString())
                .build());
        var execution = WorkflowClient.start(wf::execute, bioSpec);
        //this.wfId = execution.getWorkflowId();
        return "BIO Workflow started successfully with query: " + queryValue;
    }

    @GetMapping(value = "/citation")
    @Operation(summary = "Start an Citation workflow", 
                description = "Starts a new workflow based on the provided input.") 
    public String startCitationWorkflow(@Parameter(description = "Input data for the workflow", example = "10.1107/S0108767317098695", required = true) @RequestParam(required = true) String doi) {
        var uuid = UUID.randomUUID();
       WorkflowOptions options = WorkflowOptions.newBuilder()
            .setWorkflowId(uuid.toString()) 
            .setTaskQueue("citationQueue")
            .build();
            System.out.println("Starting Citation Workflow with DOI: " + doi);
        CitationWorkflow workflow = workflowClient.newWorkflowStub(CitationWorkflow.class, options);
        WorkflowClient.start(workflow::getCitations, doi);
        return "Citation Workflow started successfully!";
    }

    @GetMapping(value = "/publication/retrieve")
    @Operation(summary = "Publication retrieval",
                description = "Retrieve publication based on DOI.")
    public String startPublicationWorkflow(@Parameter(description = "Input data for the workflow", example = "10.1111/1751-7915.70355", required = true) @RequestParam(required = true) String doi) {
        var uuid = UUID.randomUUID();
       WorkflowOptions options = WorkflowOptions.newBuilder()
            .setWorkflowId(uuid.toString()) 
            .setTaskQueue("PublicationQueue")
            .build();
        PublicationWorkflow workflow = workflowClient.newWorkflowStub(PublicationWorkflow.class, options);
        WorkflowClient.start(workflow::retrieve, doi);
        return "Publication workflow started successfully!";
    }

    @GetMapping(value = "/crossref")
    @Operation(summary = "References crossref metadata",
                description = "Crossref based on DOI.")
    public String startCrossrefWorkflow(@Parameter(description = "Input data for the workflow", example = "10.1107/S0108767317098695", required = true) @RequestParam(required = true) String doi) {
        var uuid = UUID.randomUUID();
       WorkflowOptions options = WorkflowOptions.newBuilder()
            .setWorkflowId(uuid.toString()) 
            .setTaskQueue("CrossrefQueue")
            .build();
        CrossrefWorkflow workflow = workflowClient.newWorkflowStub(CrossrefWorkflow.class, options);
        WorkflowClient.start(workflow::cross, doi);
        return "Crossref workflow started successfully!";
    }

    // @PostMapping
    // @Operation(summary = "Start an Docling workflow", 
    //             description = "Starts a new workflow based on the provided input.") 
    // @Parameters(value = {
    //     @Parameter(name = "FlowParameters", description = "Input data for the workflow", required = true)
    // })   
    // public String startDoclingWorkflow(@RequestBody String FlowParameters) {
    //     //wfhService.handleWorkflow(stuffHere);
    //     return "Workflow started successfully!";
    // }
}

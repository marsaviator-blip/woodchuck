package org.woodchuck.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.woodchuck.temporal.workflows.MPWorkflow;
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

    // @PostMapping
    // @Operation(summary = "Start an MP workflow", 
    //             description = "Starts a new workflow based on the provided input.") 
    // @Parameters(value = {
    //     @Parameter(name = "FlowParameters", description = "Input data for the workflow", required = true)
    // })   
    // public String startMPWorkflow(@RequestBody FlowParameters flowParameters) {

    @PostMapping
    @Operation(summary = "Start an MP workflow", 
                description = "Starts a new workflow based on the provided input.") 
    @Parameters(value = {
        @Parameter(name = "MPSpec", description = "Input data for the workflow", required = true)
    })   
    public String startMPWorkflow(@RequestBody MPSpec mpSpec) {
        WorkflowOptions options = WorkflowOptions.newBuilder()
            .setWorkflowId("mp-" + "mpId") // You can generate a unique ID or use a meaningful one
            .setTaskQueue("MP_QUEUE")
            .build();
        MPWorkflow workflow = workflowClient.newWorkflowStub(MPWorkflow.class, options);
        WorkflowClient.start(workflow::processMP, mpSpec);
        return "MP wflow started successfully!";
    }

    // @PostMapping
    // @Operation(summary = "Start an BIO workflow", 
    //             description = "Starts a new workflow based on the provided input.") 
    // @Parameters(value = {
    //     @Parameter(name = "FlowParameters", description = "Input data for the workflow", required = true)
    // })   
    // public String startBIOWorkflow(@RequestBody String FlowParameters) {
    //     //wfhService.handleWorkflow(stuffHere);
    //     return "Workflow started successfully!";
    // }

    // @PostMapping
    // @Operation(summary = "Start an Citation workflow", 
    //             description = "Starts a new workflow based on the provided input.") 
    // @Parameters(value = {
    //     @Parameter(name = "FlowParameters", description = "Input data for the workflow", required = true)
    // })   
    // public String startCitationWorkflow(@RequestBody String FlowParameters) {
    //     //wfhService.handleWorkflow(stuffHere);
    //     return "Workflow started successfully!";
    // }

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

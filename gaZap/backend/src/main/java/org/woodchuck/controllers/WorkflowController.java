package org.woodchuck.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

@RestController
@RequestMapping("/gaZap/workflows") // Base path for all endpoints in this class
public class WorkflowController {
    

    @PostMapping
    @Operation(summary = "Start a new workflow", 
                description = "Starts a new workflow based on the provided input.") 
    @Parameters(value = {
        @Parameter(name = "stuffHere", description = "Input data for the workflow", required = true)
    })   
    public String startWorkflow(@RequestBody String stuffHere) {
        //wfhService.handleWorkflow(stuffHere);
        return "Workflow started successfully!";

    }
}

package org.woodchuck.zChecker.controller;

//import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
//import org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.woodchuck.zChecker.services.LookForRunningContainers;
import com.github.dockerjava.api.model.Container;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/gaZap/status")
//@RequiredArgsConstructor
public class StatusController {

    private final LookForRunningContainers lfrc;
    public StatusController(LookForRunningContainers lfrc) {
        this.lfrc = lfrc;
    }   

    @GetMapping("/containers")
    @Operation(summary = "Get container status", description = "Returns all containers with their status")
    @ApiResponses({
        // @ApiResponse(response_code = "200", description = "Successfully returned container status"),
        // @ApiResponse(response_code = "404", description = "Staus called but no containers found"),
        // @ApiResponse(response_code = "500", description = "Internal server error")
    })
    //public String getStatus() {        
        // List<Container> containers = this.lfrc.lookForRunningContainers();
        // if (containers.isEmpty()) {
        //     return "No running containers found.";
        // }
        // return "Found " + containers.size() + " running container(s).";
    //    return "yuk"; // --- IGNORE ---
    //}
    public ResponseEntity<List<Container>> getStatus() {        
        List<Container> containers = this.lfrc.lookForRunningContainers();
        if (containers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(containers);
    }   
    // other endpoints
}

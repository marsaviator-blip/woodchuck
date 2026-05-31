package org.woodchuck.zChecker.controller;

//import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
//import org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.woodchuck.zChecker.services.LookForRunningContainers;
import org.woodchuck.zChecker.services.DockerService;
import org.woodchuck.zChecker.dtos.ContainerDTO;
import com.github.dockerjava.api.model.Container;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@CrossOrigin(origins = "https://localhost:3002")
@RequestMapping("/gaZap/status/")
//@RequiredArgsConstructor
public class StatusController {

    private final LookForRunningContainers lfrc;
    private final DockerService dockerService;

    public StatusController(LookForRunningContainers lfrc, DockerService dockerService) {
        this.lfrc = lfrc;
        this.dockerService = dockerService;
    }   

    @GetMapping("/containers/rawStatus")
    @Operation(summary = "Get container raw status", description = "Returns all containers with their raw status")
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
    public ResponseEntity<List<Container>> getRawStatus() { 
        System.out.println("Received request for container status");       
        List<Container> containers = this.lfrc.lookForRunningContainers();
        if (containers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        System.out.println("Found " + containers.size() + " running container(s).");
        return ResponseEntity.ok(containers);
    }   
    
    @GetMapping("/containers/status")
    @Operation(summary = "Get container status", description = "Returns all containers with their status")
    @ApiResponses({
        // @ApiResponse(response_code = "200", description = "Successfully returned container status"),
        // @ApiResponse(response_code = "404", description = "Staus called but no containers found"),
        // @ApiResponse(response_code = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ContainerDTO>> getStatus() { 
        System.out.println("Received request for container status");
        // dockerService.getRunningContainers().forEach(container -> {
        //     System.out.println("Container Name: " + container.getNames()[0] + 
        //                        " | Status: "   + container.getStatus() + 
        //                        " | Ports: "    + container.getPorts()[0] +
        //                        " | Size: "     + container.getSizeRootFs() + " bytes" +
        //                        " | Networks: " + container.getNetworkSettings().getNetworks().keySet());
        // });
        List<ContainerDTO> containerDTOs = dockerService.getRunningContainers();
        if (containerDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();   
        }
        return ResponseEntity.ok(containerDTOs);
    }   
    
}

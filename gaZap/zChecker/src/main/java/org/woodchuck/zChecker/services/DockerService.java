package org.woodchuck.zChecker.services;

import org.woodchuck.zChecker.dtos.ContainerDTO;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
 
@Service
public class DockerService {

    private final DockerClient dockerClient;

    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public List<ContainerDTO> getRunningContainers() {
        List<Container> containers = dockerClient.listContainersCmd().exec();
        
        return containers.stream()
            .map(ContainerDTO::fromDockerContainer)
            .collect(Collectors.toList());
    }
}

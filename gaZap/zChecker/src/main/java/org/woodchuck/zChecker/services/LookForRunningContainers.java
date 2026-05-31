package org.woodchuck.zChecker.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import org.springframework.stereotype.Service;

import javax.print.Doc;

import org.woodchuck.zChecker.configs.DockerConfig;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LookForRunningContainers {

    private final DockerClient dockerClient;

    public LookForRunningContainers(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        
//        Logger.info("Setting up LookFoRunningContainers service...");
        lookForRunningContainers();
    }

    public List<Container> lookForRunningContainers() {
//        Logger.info("Looking for running containers...");

        // $ docker ps -a -s -f status=exited
        // # or 
        // $ docker container ls -a -s -f status=exited//        List<Container> containers = dockerClient.listContainersCmd().exec();
        List<Container> containers = dockerClient.listContainersCmd()
            .withShowSize(true)
            .withShowAll(true)
            .withStatusFilter(Collections.singletonList("running")).exec();
        System.out.println("Found " + containers.size() + " containers.");
        // return containers.stream()
        //         .filter(container -> config.getTargetContainerNames().contains(container.getNames()[0]))
        //         .collect(Collectors.toList());   
        for (Container container : containers) {
            System.out.println("Name: " + container.getNames()[0] + 
                               " | Status: "   + container.getStatus() + 
                               " | Ports: "    + container.getPorts()[0] +
                               " | Size: "     + container.getSizeRootFs() + " bytes" +
                               " | Networks: " + container.getNetworkSettings().getNetworks().keySet());
        }
        return containers;
    }

    // is this a docker-compose way?
    // public List<ContainerInfo> findRunningContainers() {
    //     Logger.info("Looking for running containers...");
    //     List<ContainerInfo> runningContainers = DockerUtils.getRunningContainers(config.getDockerHost());
    //     Logger.info("Found " + runningContainers.size() + " running containers.");
    //     return runningContainers.stream()
    //             .filter(container -> config.getTargetContainerNames().contains(container.getName()))
    //             .collect(Collectors.toList());
    // }
}   
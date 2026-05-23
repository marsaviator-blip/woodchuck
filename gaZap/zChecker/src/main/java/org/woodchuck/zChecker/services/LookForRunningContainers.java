package org.woodchuck.zChecker.services;

// import org.woodchuck.zChecker.model.ContainerInfo;
// import org.woodchuck.zChecker.model.ZCheckerConfig;
// import org.woodchuck.zChecker.utils.DockerUtils;
// import org.woodchuck.zChecker.utils.Logger;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import javax.print.Doc;

import org.woodchuck.zChecker.configs.DockerConfig;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

@Service
public class LookForRunningContainers {

   // private DockerConfig dockerConfig;
//   private Container container;
    private DockerClient dockerClient;
    //private DockerClientConfig config;
    //private final ZCheckerConfig config;

    public LookForRunningContainers(DockerClient dockerClient) {
//        this.container = container;
        this.dockerClient = dockerClient;
        
//        Logger.info("Setting up LookFoRunningContainers service...");
        // Any additional setup can be done here

        //  SEVERAL WAYS TO DO BUSINESS WITH DOCKER JAVA API

        // DefaultDockerClientConfig config
        // = DefaultDockerClientConfig.createDefaultConfigBuilder()
        //     .withRegistryEmail("info@baeldung.com")
        //     .withRegistryPassword("baeldung")
        //     .withRegistryUsername("baeldung")
        //     .withDockerCertPath("/home/baeldung/.docker/certs")
        //     .withDockerConfig("/home/baeldung/.docker/")
        //     .withDockerTlsVerify("1")
        //     .withDockerHost("tcp://docker.baeldung.com:2376").build();

        // DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

        // DefaultDockerClientConfig.Builder config 
        //     = DefaultDockerClientConfig.createDefaultConfigBuilder();
        // DockerClient dockerClient = DockerClientBuilder
        //     .getInstance(config)
        //     .build();

        //DockerClient dockerClient = DockerClientBuilder.getInstance().build();

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
            .exec();
//            .withStatusFilter("exited").exec();
        System.out.println("Found " + containers.size() + " containers.");
        // return containers.stream()
        //         .filter(container -> config.getTargetContainerNames().contains(container.getNames()[0]))
        //         .collect(Collectors.toList());   
        for (Container container : containers) {
            System.out.println("Name: " + container.getNames()[0] + 
                               " | Status: " + container.getStatus());// + 
//                               " | State: "  + container.getState());
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
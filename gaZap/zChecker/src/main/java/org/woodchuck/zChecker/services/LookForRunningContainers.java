package org.woodchuck.zChecker.services;

// import org.woodchuck.zChecker.model.ContainerInfo;
// import org.woodchuck.zChecker.model.ZCheckerConfig;
// import org.woodchuck.zChecker.utils.DockerUtils;
// import org.woodchuck.zChecker.utils.Logger;
import java.util.List;
import java.util.stream.Collectors;

// import com.github.dockerjava.api.DockerClient;
// import com.github.dockerjava.api.model.Container;
// import com.github.dockerjava.core.DefaultDockerClientConfig;
// import com.github.dockerjava.core.DockerClientBuilder;

public class LookForRunningContainers {

    //private final ZCheckerConfig config;

//    public LookForRunningContainers(ZCheckerConfig config) {
//        this.config = config;
//    }

    public void setup() {
//        Logger.info("Setting up LookFoRunningContainers service...");
        // Any additional setup can be done here

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
        //     DockerClient dockerClient = DockerClientBuilder
        //       .getInstance(config)
        //       .build();

//        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
    }

    public void lookingForRunningContainers() {
//        Logger.info("Looking for running containers...");

        // $ docker ps -a -s -f status=exited
        // # or 
        // $ docker container ls -a -s -f status=exited//        List<Container> containers = dockerClient.listContainersCmd().exec();
        // List<Container> containers = dockerClient.listContainersCmd()
        //     .withShowSize(true)
        //     .withShowAll(true)
        //     .withStatusFilter("exited").exec();
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
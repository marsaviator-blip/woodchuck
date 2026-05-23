package org.woodchuck.zChecker.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.model.Container;
import java.util.List;

@Configuration
public class DockerConfig {

    // @Bean
    // public DockerClient dockerClient() {
    //     DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
    //             .withDockerHost("unix:///var/run/docker.sock") // or "tcp://localhost:2375" for Windows/Remote
    //             .build();
                
    //     return DockerClientBuilder.getInstance(config).build();
    // }
    // 1. Define the DockerClient bean for interacting with the Docker daemon
    @Bean
    public DockerClient dockerClient() {
        return DockerClientBuilder.getInstance().build();
    }

    // 2. Define the specific Container bean by querying the Docker client
    @Bean
    public Container activeContainer(DockerClient dockerClient) {
        // List containers (exec() executes the command)
        List<Container> containers = dockerClient.listContainersCmd().exec();
        
        if (containers.isEmpty()) {
            throw new RuntimeException("No running Docker containers found.");
        }
        
        // Return the first container in the list (adjust filter logic as needed)
        return containers.get(0);
    }
}

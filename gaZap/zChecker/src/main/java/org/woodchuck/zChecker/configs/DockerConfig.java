package org.woodchuck.zChecker.configs;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class DockerConfig {

    @Bean
    public DockerClient dockerClient() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                // Use "unix:///var/run/docker.sock" for Linux/macOS or "tcp://localhost:2375" for Windows
                .withDockerHost("unix:///var/run/docker.sock") 
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }
}



// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import com.github.dockerjava.api.DockerClient;
// import com.github.dockerjava.core.DefaultDockerClientConfig;
// import com.github.dockerjava.core.DockerClientBuilder;
// import com.github.dockerjava.core.DockerClientConfig;
// import com.github.dockerjava.transport.DockerHttpClient;
// import com.github.dockerjava.transport.ZerodepDockerHttpClient;

// import java.util.List;

// @Configuration
// public class DockerConfig {

//     @Bean
//     public DockerClient dockerClient() {
//         DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        
//         DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
//                 .dockerHost(config.getDockerHost())
//                 .sslConfig(config.getSSLConfig())
//                 .build();

//         return DockerClientBuilder.getInstance(config)
//                 .withDockerHttpClient(httpClient)
//                 .build();
//     }
// }

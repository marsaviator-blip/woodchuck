package org.woodchuck.zChecker.dtos;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerDTO {
    private String id;
    private String name;
    private String image;
    private String status;
//    private ContainerPort[] ports;
    private String[] networks;
//    private long size;

    public ContainerDTO() {}
    public ContainerDTO(String id, String name, String image, String status, String[] networks) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
//        this.ports = ports;
        this.networks = networks;
//        this.size = size;
    }

    // Builder Pattern
    public static class Builder {
        private String id;
        private String name;
        private String image;
        private String status;
//        private ContainerPort[] ports;
        private String[] networks;
//        private long size;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder image(String image) { this.image = image; return this; }
        public Builder status(String status) { this.status = status; return this; }
//        public Builder ports(ContainerPort[] ports) { this.ports = ports; return this; }
        public Builder networks(String[] networks) { this.networks = networks; return this; }
//        public Builder size(long size) { this.size = size; return this; }   

        public ContainerDTO build() {
            return new ContainerDTO(id, name, image, status, networks);
        }
    }

    // Mapper Method
    public static ContainerDTO fromDockerContainer(com.github.dockerjava.api.model.Container container) {
        String containerName = (container.getNames() != null && container.getNames().length > 0) 
            ? container.getNames()[0] : "N/A";
            
        return new ContainerDTO.Builder()
            .id(container.getId())
            .name(containerName)
            .image(container.getImage())
            .status(container.getStatus())
    //        .ports(container.getPorts().
            .networks(container.getNetworkSettings() != null 
                ? container.getNetworkSettings().getNetworks().keySet().toArray(new String[0]) 
                : new String[0])
    //        .size(container.getSizeRootFs())
            .build();                                                                                                                          
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // public ContainerPort[] getPorts() { return ports; }
    // public void setPorts(ContainerPort[] ports) { this.ports = ports; }

    public String[] getNetworks() { return networks; }
    public void setNetworks(String[] networks) { this.networks = networks; }

    // public long getSize() { return size; }
    // public void setSize(long size) { this.size = size; }
}
package com.mars.my_app.inventory;

import jakarta.validation.constraints.Size;
import java.util.List;


public class InventoryDTO {

    private Long id;

    @Size(max = 25)
    private String species;

    private Double lengthInInches;

    private Double widthInInches;

    private Double thicknesInInches;

    private List<@Size(max = 255) String> condition;

    @Size(max = 25)
    private String type;

    @Size(max = 25)
    private String liveEdge;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(final String species) {
        this.species = species;
    }

    public Double getLengthInInches() {
        return lengthInInches;
    }

    public void setLengthInInches(final Double lengthInInches) {
        this.lengthInInches = lengthInInches;
    }

    public Double getWidthInInches() {
        return widthInInches;
    }

    public void setWidthInInches(final Double widthInInches) {
        this.widthInInches = widthInInches;
    }

    public Double getThicknesInInches() {
        return thicknesInInches;
    }

    public void setThicknesInInches(final Double thicknesInInches) {
        this.thicknesInInches = thicknesInInches;
    }

    public List<String> getCondition() {
        return condition;
    }

    public void setCondition(final List<String> condition) {
        this.condition = condition;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getLiveEdge() {
        return liveEdge;
    }

    public void setLiveEdge(final String liveEdge) {
        this.liveEdge = liveEdge;
    }

}

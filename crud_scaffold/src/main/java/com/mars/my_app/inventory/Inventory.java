package com.mars.my_app.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.OffsetDateTime;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
public class Inventory {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 25)
    private String species;

    @Column
    private Double lengthInInches;

    @Column
    private Double widthInInches;

    @Column
    private Double thicknesInInches;

    @Column(columnDefinition = "json", name = "\"condition\"")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> condition;

    @Column(length = 25)
    private String type;

    @Column(length = 25)
    private String liveEdge;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

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

    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final OffsetDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}

package org.woodchuck.entities;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("TableRow")
public class TableRowEntity {

    @Id @GeneratedValue
    private Long internalNeoId;
    
    private int rowIndex;
    private String rowDataJson; // Stores row fields as queryable JSON data strings

    public TableRowEntity() {}
    public TableRowEntity(int rowIndex, String rowDataJson) {
        this.rowIndex = rowIndex;
        this.rowDataJson = rowDataJson;
    }

    public Long getInternalNeoId() {
        return internalNeoId;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public String getRowDataJson() {
        return rowDataJson;
    }
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setRowDataJson(String rowDataJson) {
        this.rowDataJson = rowDataJson;
    }
}

package org.woodchuck.dtos;

public class FlowParameters {
    private String elementId; // or a list or something else
    private boolean doStructureCall = false;
    private boolean doNeo4JCall = false;
    private boolean doProvenanceCall = false;
    private boolean doDOICall = false;
    private boolean doCIFCall = false;
    private boolean persistToFilesystem = false;
    private boolean persistToDatabase = false; //in UI a dialog could pop up to choose OpenSearch or Postgre PGVector

    public FlowParameters() {
    }

    public FlowParameters(String elementId) {
        this.elementId = elementId;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public boolean isDoStructureCall() {
        return doStructureCall;
    }   

    public void setDoStructureCall(boolean doStructureCall) {
        this.doStructureCall = doStructureCall;
    }

    public boolean isDoNeo4JCall() {
        return doNeo4JCall;
    }

    public void setDoNeo4JCall(boolean doNeo4JCall) {
        this.doNeo4JCall = doNeo4JCall;
    }

    public boolean isDoProvenanceCall() {
        return doProvenanceCall;
    }

    public void setDoProvenanceCall(boolean doProvenanceCall) {
        this.doProvenanceCall = doProvenanceCall;
    }

    public boolean isDoDOICall() {
        return doDOICall;
    }

    public void setDoDOICall(boolean doDOICall) {
        this.doDOICall = doDOICall;
    }   

    public boolean isDoCIFCall() {
        return doCIFCall;
    }   

    public void setDoCIFCall(boolean doCIFCall) {
        this.doCIFCall = doCIFCall;
    }

    public boolean isPersistToFilesystem() {
        return persistToFilesystem;
    }   

    public void setPersistToFilesystem(boolean persistToFilesystem) {
        this.persistToFilesystem = persistToFilesystem;
    }   

    public boolean isPersistToDatabase() {
        return persistToDatabase;
    }

    public void setPersistToDatabase(boolean persistToDatabase) {
        this.persistToDatabase = persistToDatabase;
    }

    
}

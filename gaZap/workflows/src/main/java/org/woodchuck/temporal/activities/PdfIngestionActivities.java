package org.woodchuck.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;

import org.woodchuck.dtos.DocumentAnalysisResult;

@ActivityInterface
public interface PdfIngestionActivities {

    @ActivityMethod
    DocumentAnalysisResult extractReferenceSection(String pdfFilePath);

    @ActivityMethod
    List<String> splitReferences(DocumentAnalysisResult analysisResult);

    @ActivityMethod
    void saveToNeo4jGraph(String title, List<String> crossRefJsonRecords);
}

package org.woodchuck.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;

@ActivityInterface
public interface PdfIngestionActivities {

    @ActivityMethod
    String extractReferenceSection(String pdfFilePath);

    @ActivityMethod
    List<String> splitReferences(String rawReferenceSection);

    @ActivityMethod
    void saveToNeo4jGraph(String title, List<String> crossRefJsonRecords);
}

package org.woodchuck.temporal.activities;

import org.woodchuck.dtos.CitedReferencesResult;
import org.woodchuck.dtos.CrossrefSearchResponse;
import org.woodchuck.dtos.CrossrefXmlResponse;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CrossrefActivities {

    CrossrefXmlResponse getWorks(String doi);
    CitedReferencesResult getWorksBy(String citeKey, String title, String author);
}

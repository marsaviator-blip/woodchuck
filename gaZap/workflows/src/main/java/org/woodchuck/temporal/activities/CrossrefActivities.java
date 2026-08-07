package org.woodchuck.temporal.activities;

import org.woodchuck.dtos.CitedReferencesResult;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CrossrefActivities {

    CitedReferencesResult getWorks(String doi);
    CitedReferencesResult getWorksBy(String citeKey, String title, String author);
}

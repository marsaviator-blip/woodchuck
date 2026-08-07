package org.woodchuck.temporal.activities;

import org.woodchuck.dtos.CitedReferencesResult;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface CrossrefDatabaseActivities {

    @ActivityMethod
    boolean isRecordCached(String doi);

    @ActivityMethod
    CitedReferencesResult findCachedRecord(String citeKey, String doi);

    @ActivityMethod
    void saveResponse(String doi, String journalTitle, String rawXml, String rawJson);
}

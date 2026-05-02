package org.woodchuck.temporal.activities;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CitationActivities {

    String getCitations(String doi);

}

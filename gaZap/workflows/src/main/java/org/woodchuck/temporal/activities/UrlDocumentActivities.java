package org.woodchuck.temporal.activities;

import io.temporal.activity.ActivityInterface;

@ActivityInterface

public interface UrlDocumentActivities {

    byte[] fetch(String url);
}

package org.woodchuck.temporal.activities;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface PublicationActivities {

        String getPublication(String doi);

}

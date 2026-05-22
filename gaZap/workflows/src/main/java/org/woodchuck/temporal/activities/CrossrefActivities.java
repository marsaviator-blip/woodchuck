package org.woodchuck.temporal.activities;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CrossrefActivities {

    String getWorks(String doi);
    String getWorksBy(String title, String author);
}

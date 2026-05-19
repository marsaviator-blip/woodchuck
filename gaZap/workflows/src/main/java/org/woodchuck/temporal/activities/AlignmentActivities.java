package org.woodchuck.temporal.activities;

import java.util.List;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface AlignmentActivities {

    String execute(List<String> entries);
}

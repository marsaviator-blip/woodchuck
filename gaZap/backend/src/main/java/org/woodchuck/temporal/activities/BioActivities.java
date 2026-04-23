package org.woodchuck.temporal.activities;

import java.util.List;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface BioActivities {
    List<String> searchIdentifiers(String query);

    List<String> fetchEntries(List<String> entries);
}

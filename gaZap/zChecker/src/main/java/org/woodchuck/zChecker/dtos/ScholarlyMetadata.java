package org.woodchuck.zChecker.controllers;

import java.util.List;

public record ScholarlyMetadata(
    String title,
    List<String> authors,
    String publicationDate,
    List<String> fundingInstitutions
) {}
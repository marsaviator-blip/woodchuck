package org.woodchuck.zChecker.dtos;

import java.time.LocalDateTime;

public record StreamConceptNode(
    Long id,
    String prompt,
    StreamNodeType nodeType,
    LocalDateTime timestamp,
    String userId
) {}

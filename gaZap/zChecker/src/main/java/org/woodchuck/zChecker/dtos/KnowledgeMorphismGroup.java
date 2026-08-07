package org.woodchuck.zChecker.dtos;
package com.example.dto;

import java.time.LocalDateTime;
import java.util.List;

public record KnowledgeMorphismGroup(
    String groupId,
    LocalDateTime savedAt,
    String focusArea,
    String category,
    String subject,
    String topic,
    List<StreamConceptNode> curatedNodes
) {}


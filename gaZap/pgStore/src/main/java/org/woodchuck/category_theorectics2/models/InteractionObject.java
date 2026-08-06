package org.woodchuck.category_theorectics2.models;

import org.woodchuck.category_theorectics2.models.InteractionType;
import java.time.Instant;

public record InteractionObject(
    String id,
    InteractionType type, // PROMPT, AI_RESPONSE, WEB_CLIP, NOTE
    String content,
    Instant timestamp
) {}



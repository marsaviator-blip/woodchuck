package org.woodchuck.dtos;

import java.util.List;

/**
 * Data model capturing the snapshot state of an in-flight discovery pipeline.
 * This object is returned synchronously by Temporal @QueryMethod triggers 
 * to drive UI updates, status text, and tables on the Vue 3 dashboard workspace.
 */
public record LivePipelineState(
    /**
     * Represents the active stage of execution.
     * Expected values: "INITIALIZED", "DISCOVERING_URLS", "PARSING_DOCLING", 
     * "GENERATING_AI_THEMES", "AWAITING_HUMAN_REVIEW", "PERSISTING_RESULTS", "COMPLETED"
     */
    String currentStep,

    /**
     * The running log of target website links discovered by Gemini's custom web search tool tool.
     */
    List<String> discoveredUrls,

    /**
     * The preliminary, unstructured thematic dataset returned by the AI before 
     * a researcher interacts with the card-sorting interface.
     */
    ThematicAnalysisResponse temporaryThemes
) {}


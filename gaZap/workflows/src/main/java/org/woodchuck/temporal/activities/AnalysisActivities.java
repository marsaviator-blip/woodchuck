package org.woodchuck.temporal.activities;

import org.woodchuck.dtos.HumanReviewEdits;
import org.woodchuck.dtos.ThematicAnalysisResponse;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * Interface defining the execution tasks for the Unstructured Thematic Analysis Engine.
 * Annotated with @ActivityInterface to comply with Temporal SDK orchestration standards.
 */
@ActivityInterface
public interface AnalysisActivities {
    
    /**
     * Step 1: Uses Gemini to discover relevant raw web page URLs based on a target subject.
     *
     * @param subject the topic of interest submitted by the researcher
     * @return a list of live website URL strings discovered by the model
     */
    @ActivityMethod
    List<String> discoverUrlsWithGemini(String subject, int maxLinks);

    /**
     * Step 2: Uses Arconia Docling to strip boilerplate data, layout advertisements, 
     * and site tracking metrics from raw target web URLs.
     *
     * @param urls the list of website links discovered in Step 1
     * @return a collection of native Spring AI Document objects containing clean text blocks
     */
    @ActivityMethod
    List<Document> parsePagesWithDocling(List<String> urls);

    /**
     * Step 3: Passes the parsed text document blocks to Gemini, executing Chain-of-Thought
     * prompts to identify underlying data themes and rationale justifications.
     *
     * @param chunks the clean text document chunks to evaluate
     * @return a structured thematic analysis report containing themes, quotes, and reasoning logic
     */
    @ActivityMethod
    ThematicAnalysisResponse generateThemesWithReasoning(List<Document> chunks);

    /**
     * Step 4: Persists the finalized, human-approved thematic data structure 
     * into downstream persistence database engines.
     *
     * @finalResults the final, consolidated data structure approved by the researcher
     */
    @ActivityMethod
    void saveFinalResults(ThematicAnalysisResponse finalResults);
}



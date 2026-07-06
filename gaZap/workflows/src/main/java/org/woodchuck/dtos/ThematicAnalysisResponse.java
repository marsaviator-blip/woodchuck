package org.woodchuck.dtos;

import java.util.List;

/**
 * Main wrapper returned by Gemini representing the global thematic state.
 */
public record ThematicAnalysisResponse(
        List<ThemeEvolution> extractedThemes) {

    /**
     * Individual theme unit holding explicit reasoning steps and verbatim source
     * proof.
     */
    record ThemeEvolution(
            String themeName,
            String underlyingLogic, // Captures the AI's "why" reasoning step
            List<String> verbatimQuotes, // Tracked evidence for human verification
            double confidenceScore) {
    }
}

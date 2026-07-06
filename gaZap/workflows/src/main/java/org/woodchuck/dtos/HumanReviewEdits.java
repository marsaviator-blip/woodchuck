package org.woodchuck.dtos;

import java.util.List;

// Main Wrapper holding all modifications submitted by the human researcher
public record HumanReviewEdits(
        List<ThemeMergeInstruction> merges,
        List<String> deletedThemeNames,
        List<ThemeRenameInstruction> renames) {

    // Target specific sources to combine into a uniform bucket
    record ThemeMergeInstruction(
            List<String> sourceThemeNames, // e.g., ["Eco Architecture", "Sustainable Buildings"]
            String targetThemeName // e.g., "Green Building Infrastructure"
    ) {
    }

    // For quick title adjustments
    record ThemeRenameInstruction(
            String originalName,
            String updatedName) {
    }
}

package org.woodchuck.zChecker.dtos;

import java.util.List;

// Main wrapper payload for the Author analytics view
public record AuthorPayloadDTO(
    Long authorCount,
    List<AuthorInfo> authorList,
    List<AuthorWithDocs> authorsWithDocs
) {

    // Individual simplified Author structure
    public record AuthorInfo(String id, String name) {}

    // Structural mapping linking an Author node to an array of related Document nodes
    public record AuthorWithDocs(
        String authorId, 
        String authorName, 
        List<DocumentInfo> relatedDocuments
    ) {

        // Individual simplified Document structure
        public record DocumentInfo(String id, String title) {}
    }

}

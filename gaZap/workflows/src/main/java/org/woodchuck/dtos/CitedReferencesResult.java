package org.woodchuck.dtos;

import java.util.List;

public record CitedReferencesResult(String citeKey, CrossrefSearchResponse crossrefSearchResponse) {

    public CitedReferencesResult{
        
    }
}

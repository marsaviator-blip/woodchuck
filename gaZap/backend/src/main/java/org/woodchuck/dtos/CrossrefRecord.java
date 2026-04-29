package org.woodchuck.dtos;

import java.util.List;

public record CrossrefRecord(List<String> issnData, List<String> doiData) {
    public CrossrefRecord {
        if (issnData == null) {
            issnData = List.of();
        }
        if (doiData == null) {
            doiData = List.of();
        }
    }
}

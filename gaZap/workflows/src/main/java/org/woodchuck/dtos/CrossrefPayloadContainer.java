package org.woodchuck.dtos;

import org.woodchuck.dtos.CrossrefXmlResponse;
import org.woodchuck.dtos.CrossrefSearchResponse;

public record CrossrefPayloadContainer(
    CrossrefXmlResponse parsedResponse,
    CrossrefSearchResponse searchResponse,
    String xmlRawPayload,
    String jsonRawPayload) {

}

package org.woodchuck.controllers;

import io.arconia.docling.api.DoclingServeApi;
import io.arconia.docling.api.model.ConvertDocumentRequest;
import io.arconia.docling.api.model.ConvertDocumentResponse;  
import io.arconia.docling.api.model.HttpSource;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.net.URI;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;    
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/convert")
class DocumentProcessingController {

  private final DoclingServeApi doclingServeApi;

  DocumentProcessingController(DoclingServeApi doclingServeApi) {
    this.doclingServeApi = doclingServeApi;
  }

  @GetMapping("/http")
  String convertDocumentFromHttp(@RequestParam("url") String url) {
    ConvertDocumentResponse response = doclingServeApi
      .convertSource(ConvertDocumentRequest.builder()
        .source(HttpSource.builder().url(URI.create(url)).build())
        .build());
      return response.getDocument().getMarkdownContent();
  }

}

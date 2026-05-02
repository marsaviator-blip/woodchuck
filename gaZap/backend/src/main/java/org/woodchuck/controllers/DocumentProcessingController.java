package org.woodchuck.controllers;

import ai.docling.serve.api.convert.request.ConvertDocumentRequest;
import ai.docling.serve.api.convert.response.ConvertDocumentResponse;
import ai.docling.serve.api.DoclingServeApi;
import ai.docling.serve.api.convert.request.source.HttpSource;

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
@RequestMapping("/gaZap/document/processing")
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
      return response.getResponseType().name();
  }
}

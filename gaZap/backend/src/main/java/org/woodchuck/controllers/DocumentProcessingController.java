package org.woodchuck.controllers;

import ai.docling.serve.api.convert.request.ConvertDocumentRequest;
import ai.docling.serve.api.convert.response.ConvertDocumentResponse;
import ai.docling.serve.api.DoclingServeApi;
import ai.docling.serve.api.convert.request.source.FileSource;
import ai.docling.serve.api.convert.request.source.HttpSource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
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

  @GetMapping("/http/{url}")
  @Operation(summary = "Use Docling to convert a document from a given URL", 
                description = "will provide more description soon.\n"+
                "Cut n paste a url to a document, and Docling will convert it to a structured format.  ") 
  String convertDocumentFromHttp(@RequestParam("url") String url) {
    ConvertDocumentResponse response = doclingServeApi
      .convertSource(ConvertDocumentRequest.builder()
        .source(HttpSource.builder().url(URI.create(url)).build())
        .build());
      return response.getResponseType().name();
  }

  @GetMapping("/file/{fullpath}")
  @Operation(summary = "Use Docling to convert a document from a local file", 
                description = "will provide more description soon.\n"+
                "Upload a file, and Docling will convert it to a structured format.  \n"+
              "temporarily hardcoded to read from classpath resource 'documents/story.pdf'") 
  String convertDocumentFromFile(@RequestParam("fullpath") String fullpath) throws IOException {
    Resource file = new ClassPathResource("documents/story.pdf");
    String base64File = Base64.getEncoder().encodeToString(file.getContentAsByteArray());
    ConvertDocumentResponse response = doclingServeApi
      .convertSource(ConvertDocumentRequest.builder()
        .source(FileSource.builder()
          .filename("story.pdf")
          .base64String(base64File)
          .build())
        .build());
    return response.getResponseType().name();
  }

}

package org.woodchuck.controllers;

import ai.docling.serve.api.convert.request.ConvertDocumentRequest;
import ai.docling.serve.api.convert.response.ConvertDocumentResponse;
import ai.docling.serve.api.convert.request.source.FileSource;
import ai.docling.serve.api.convert.request.source.HttpSource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.woodchuck.services.DoclingAsyncService;

@RestController
@RequestMapping("/gaZap/document/processing")
@Validated
public class DocumentProcessingController {

    private final DoclingAsyncService doclingAsyncService;

    public DocumentProcessingController(DoclingAsyncService doclingAsyncService) {
        this.doclingAsyncService = doclingAsyncService;
    }

    @GetMapping("/http")
    @Operation(summary = "Convert a document from a URL",
            description = "Provide a URL and Docling will convert it to a structured format.")
    public CompletableFuture<String> convertDocumentFromHttp(@RequestParam("url") @NotBlank String url) {
        return doclingAsyncService.processDocumentAsync(
                ConvertDocumentRequest.builder()
                        .source(HttpSource.builder().url(URI.create(url)).build())
                        .build())
                .thenApply(ConvertDocumentResponse::getResponseType)
                .thenApply(Enum::name);
    }

    @GetMapping("/file")
    @Operation(summary = "Convert a local file",
            description = "Provide a local file path and Docling will convert the file.")
    public CompletableFuture<String> convertDocumentFromFile(@RequestParam("fullpath") @NotBlank String fullpath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path filePath = Paths.get(fullpath);
                byte[] fileBytes = Files.readAllBytes(filePath);
                String base64File = Base64.getEncoder().encodeToString(fileBytes);
                String filename = filePath.getFileName().toString();
                ConvertDocumentResponse response = doclingAsyncService.processDocumentAsync(
                        ConvertDocumentRequest.builder()
                                .source(FileSource.builder()
                                        .filename(filename)
                                        .base64String(base64File)
                                        .build())
                                .build())
                        .join();
                return response.getResponseType().name();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

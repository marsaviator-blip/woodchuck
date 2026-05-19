package org.woodchuck.controllers;

// import ai.docling.serve.api.chunk.request.HybridChunkDocumentRequest;
// import ai.docling.serve.api.chunk.response.ChunkDocumentResponse;
// import ai.docling.serve.api.convert.request.ConvertDocumentRequest;
// import ai.docling.serve.api.convert.response.ConvertDocumentResponse;
// import ai.docling.serve.api.convert.request.source.FileSource;
// import ai.docling.serve.api.convert.request.source.HttpSource;

import java.io.IOException;
//import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
//import java.util.concurrent.CompletableFuture;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.woodchuck.services.DoclingAsyncService;
import org.woodchuck.services.VSmessageSender;

@RestController
@RequestMapping("/gaZap/document/processing")
@Validated
public class DocumentProcessingController {

    //private final DoclingAsyncService doclingAsyncService;
    private final VSmessageSender messageSender;

    public DocumentProcessingController(VSmessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @PostMapping("/http")
    @Operation(summary = "Convert a document from a URL",
            description = "Provide a URL and Docling will convert it to a structured format.")
    public String convertDocumentFromHttp(@RequestParam("url") @NotBlank String url) {
    //   CompletableFuture<ChunkDocumentResponse> future = doclingAsyncService.processDocumentAsync(
    //             HybridChunkDocumentRequest.builder()
    //                     .source(HttpSource.builder().url(URI.create(url)).build())
    //                     .build());
    //   return future.state().toString();
        messageSender.sendMessage(url);
        
        return ("Message sent to broker for "+url);
    }

    @GetMapping("/file")
    @Operation(summary = "Convert a local file",
            description = "Provide a local file path and Docling will convert the file.")
    public String convertDocumentFromFile(@RequestParam("fullpath") @NotBlank String fullpath) {
            try {
                Path filePath = Paths.get(fullpath);
                byte[] fileBytes = Files.readAllBytes(filePath);
                String base64File = Base64.getEncoder().encodeToString(fileBytes);
                String filename = filePath.getFileName().toString();
                // CompletableFuture<ChunkDocumentResponse> future = doclingAsyncService.processDocumentAsync(
                //     HybridChunkDocumentRequest.builder()
                //         .source(FileSource.builder()
                //                 .filename(filename)
                //                 .base64String(base64File)
                //                 .build())
                //         .build());
                // return future.state().toString();
                return "Not working";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}

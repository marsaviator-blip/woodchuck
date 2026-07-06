package org.woodchuck.zChecker.controllers;

import org.woodchuck.zChecker.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "http://localhost:3002") // Adjust to your Vue dev server URL
public class AuthorController {

    // private final Author author;
    // public AuthorController(Author author) {
    //     this.author = author;

    // }

    @GetMapping
    public ResponseEntity<Page<Author>> getAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        // stubbed out
        List<Author> authors = List.of(new Author(), new Author());
        Page<Author> authorPage = new PageImpl<>(authors, pageable, authors.size());
        
        return ResponseEntity.ok(authorPage);
    }
}

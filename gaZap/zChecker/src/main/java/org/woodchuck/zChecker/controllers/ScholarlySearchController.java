package org.woodchuck.zChecker.controllers;

import org.woodchuck.zChecker.dtos.SearchResultDTO;
import org.woodchuck.zChecker.services.ScholarlyService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ScholarlySearchController {

    private final ScholarlyService scholarlyService;

    public ScholarlySearchController(ScholarlyService scholarlyService) {
        this.scholarlyService = scholarlyService;
    }

    @GetMapping("/search")
    public List<SearchResultDTO> search(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "type", defaultValue = "all") String type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return scholarlyService.searchGraph(query, type, pageable);
    }
}

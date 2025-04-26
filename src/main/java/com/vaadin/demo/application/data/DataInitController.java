package com.vaadin.demo.application.data;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to initialize sample data via HTTP request
 */
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataInitController {

    private final SampleDataService sampleDataService;

    /**
     * Initialize sample data
     * Can be triggered with: curl -X POST http://localhost:8080/api/data/init
     */
    @PostMapping("/init")
    public ResponseEntity<String> initializeData() {
        sampleDataService.loadSampleData();
        return ResponseEntity.ok("Sample data initialized successfully");
    }
}
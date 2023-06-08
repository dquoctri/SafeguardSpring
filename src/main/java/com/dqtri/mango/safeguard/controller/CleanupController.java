package com.dqtri.mango.safeguard.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cleanup")
@Profile({"development", "formal_test"})
@Tag(name = "Cleanup API", description = "Endpoints for cleanup data for testing develop only")
public class CleanupController {

    @DeleteMapping
    public ResponseEntity<String> cleanUpData() {
        // Logic to clean up data
        return ResponseEntity.ok("Data cleaned up successfully");
    }

}

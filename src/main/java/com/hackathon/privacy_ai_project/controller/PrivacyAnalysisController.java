package com.hackathon.privacy_ai_project.controller;

import com.hackathon.privacy_ai_project.dto.PrivacyAnalysisRequest;
import com.hackathon.privacy_ai_project.dto.PrivacyAnalysisResponse;
import com.hackathon.privacy_ai_project.service.PrivacyAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin()
public class PrivacyAnalysisController {

    private final PrivacyAnalysisService privacyAnalysisService;

    @PostMapping("/analyzePrivacy")
    public ResponseEntity<PrivacyAnalysisResponse> analyzePrivacy(@RequestBody PrivacyAnalysisRequest request) {
        try {

            log.info("Received privacy analysis request for URL: {}", request.getUrl());

            // Validate input
            if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            PrivacyAnalysisResponse response = privacyAnalysisService.analyzePrivacyPolicy(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing privacy analysis request", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Privacy AI Service is running");
    }
}

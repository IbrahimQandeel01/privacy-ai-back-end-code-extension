package com.hackathon.privacy_ai_project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyAnalysisResponse {
    private Long id;
    private String url;
    private String analysisResult;
    private String timestamp;
}

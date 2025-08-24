package com.hackathon.privacy_ai_project.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.hackathon.privacy_ai_project.dto.PrivacyAnalysisRequest;
import com.hackathon.privacy_ai_project.dto.PrivacyAnalysisResponse;
import com.hackathon.privacy_ai_project.entity.PrivacyAnalysis;
import com.hackathon.privacy_ai_project.repository.PrivacyAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivacyAnalysisService {

    private final PrivacyAnalysisRepository privacyAnalysisRepository;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    public PrivacyAnalysisResponse analyzePrivacyPolicy(PrivacyAnalysisRequest request) {
        try {
            log.info("Starting privacy policy analysis for URL: {}", request.getUrl());

            // First, check if this URL has already been analyzed
            Optional<PrivacyAnalysis> existingAnalysis = privacyAnalysisRepository
                    .findFirstByUrlOrderByRequestTimeDesc(request.getUrl());

            PrivacyAnalysis entity;

            if (existingAnalysis.isPresent()) {
                log.info("Found existing analysis for URL: {}, returning cached result", request.getUrl());
                entity = existingAnalysis.get();
            } else {
                log.info("No existing analysis found for URL: {}, calling Gemini AI", request.getUrl());

                // Call Gemini AI to analyze the privacy policy
                String analysisResult = callGeminiAI(request.getUrl());

                // Save to database
                entity = new PrivacyAnalysis();
                entity.setUrl(request.getUrl());
                entity.setAnalysisResult(analysisResult);

                entity = privacyAnalysisRepository.save(entity);
            }

            // Return response
            PrivacyAnalysisResponse response = new PrivacyAnalysisResponse();
            response.setId(entity.getId());
            response.setUrl(entity.getUrl());
            response.setAnalysisResult(entity.getAnalysisResult());
            response.setTimestamp(entity.getRequestTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            log.info("Privacy policy analysis completed for URL: {}", request.getUrl());
            return response;

        } catch (Exception e) {
            log.error("Error analyzing privacy policy for URL: {}", request.getUrl(), e);
            throw new RuntimeException("Failed to analyze privacy policy: " + e.getMessage(), e);
        }
    }

    private String callGeminiAI(String url) {
        try {
            log.info("Calling Gemini AI for URL analysis: {}", url);

            // Create Gemini client
            Client client = Client.builder().apiKey(geminiApiKey).build();

            // Create the prompt for privacy policy analysis
            String prompt = String.format("""
                Please analyze the privacy policy of the website at the following URL: %s
                
                Visit the privacy policy page and provide a brief analysis including:
                
                1. **Data Collection**: What personal data is collected
                2. **Data Usage**: How data is used
                3. **Data Sharing**: Third-party data sharing practices
                4. **User Rights**: User data rights (access, deletion, etc.)
                5. **Security**: Security measures in place
                6. **Recommendations**: Brief recommendations for users
                7. **Risk Level**: Overall privacy risk assessment
                
                Keep each response brief and concise (1-2 sentences maximum per field).
                If you cannot access the privacy policy, set accessible to false.
                
                Format your response as a JSON object with the following structure:
                {
                  "accessible": boolean,
                  "summary": "Brief summary of the privacy policy",
                  "dataCollection": "Details about data collection",
                  "dataUsage": "Details about data usage",
                  "dataSharing": "Details about data sharing",
                  "userRights": "Details about user rights",
                  "security": "Details about security measures",
                  "recommendations": "Recommendations for users",
                  "riskLevel": "LOW/MEDIUM/HIGH based on privacy practices"
                }
                """, url);

            // Generate content using Gemini AI
            GenerateContentResponse response = client.models.generateContent(
                "gemini-1.5-flash",
                prompt,
                null
            );

            log.info("Successfully received response from Gemini AI");

            // Clean the response (remove markdown formatting if present)
            String cleanedResponse = response.text()
                .replace("```json", "")
                .replace("```", "")
                .trim();

            return cleanedResponse;

        } catch (Exception e) {
            log.error("Error calling Gemini AI: ", e);
            throw new RuntimeException("Failed to get analysis from Gemini AI: " + e.getMessage(), e);
        }
    }
}

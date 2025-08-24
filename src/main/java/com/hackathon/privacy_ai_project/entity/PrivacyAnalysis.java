package com.hackathon.privacy_ai_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "privacy_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "analysis_result", columnDefinition = "LONGTEXT")
    private String analysisResult;

    @PrePersist
    protected void onCreate() {
        requestTime = LocalDateTime.now();
    }
}

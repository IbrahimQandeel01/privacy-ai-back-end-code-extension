package com.hackathon.privacy_ai_project.repository;

import com.hackathon.privacy_ai_project.entity.PrivacyAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivacyAnalysisRepository extends JpaRepository<PrivacyAnalysis, Long> {
    Optional<PrivacyAnalysis> findFirstByUrlOrderByRequestTimeDesc(String url);
}

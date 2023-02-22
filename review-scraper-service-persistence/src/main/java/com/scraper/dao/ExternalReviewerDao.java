package com.scraper.dao;

import com.scraper.data.model.ExternalReviewer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalReviewerDao extends JpaRepository<ExternalReviewer, Long> {
}

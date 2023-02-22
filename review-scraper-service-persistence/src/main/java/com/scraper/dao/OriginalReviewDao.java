package com.scraper.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.scraper.data.model.OriginalReview;

public interface OriginalReviewDao extends JpaRepository<OriginalReview, Long> {
}

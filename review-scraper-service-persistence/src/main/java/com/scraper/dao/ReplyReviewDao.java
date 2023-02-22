package com.scraper.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.scraper.data.model.ReplyReview;

public interface ReplyReviewDao extends JpaRepository<ReplyReview, Long> {
}

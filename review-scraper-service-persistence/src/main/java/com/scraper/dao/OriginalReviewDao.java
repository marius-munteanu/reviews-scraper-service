package com.scraper.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.scraper.data.model.OriginalReview;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OriginalReviewDao extends JpaRepository<OriginalReview, Long> {
    @Query("SELECT o.reviewExternalId FROM OriginalReview o where o.partId=:partId")
    List<Long> getAllReviewExternalIdByPartId(String partId);

    Optional<List<OriginalReview>> findAllByPartId(String partId);

    Optional<OriginalReview> findTop1ByPartIdOrderByCreatedOnDesc(String partId);
}

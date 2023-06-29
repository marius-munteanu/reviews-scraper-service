package com.scraper.app.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Getter
public class RevisedRatingDto {
    private int totalReviews;
    private int remainingReviews;
    private BigDecimal revisedRating;
    private int unverifiedReviews;
    private ReviewsMetaInfo reviewsMetaInfo;
    private Map<String, Integer> reviewsCreatedOnTheSameDay;
}

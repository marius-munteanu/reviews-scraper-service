package com.scraper.app.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class OriginalRatingDto {
    private int totalReviews;
    private BigDecimal originalRating;
}

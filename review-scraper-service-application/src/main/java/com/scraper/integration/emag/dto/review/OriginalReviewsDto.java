package com.scraper.integration.emag.dto.review;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OriginalReviewsDto {
    private List<OriginalReviewDto> originalReviewDtos;
    private String pdId;
    private int numberOfReviews;
}

package com.scraper.integration.emag.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {
    private String reviewId;
    private String pdId;
    private String reviewComment;
    private Long likes;
    private boolean isVerified;
    private String timestamp;
}

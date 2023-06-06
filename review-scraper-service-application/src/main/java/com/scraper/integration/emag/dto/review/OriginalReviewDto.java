package com.scraper.integration.emag.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OriginalReviewDto {
    private OriginalReviewerDto originalReviewerDto;
    private List<OriginalReviewDto> replyReviews;

    private String title;
    private String content;
    private long reviewId;
    private int numberOfVotes;
    private boolean isVerifiedPurchase;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}

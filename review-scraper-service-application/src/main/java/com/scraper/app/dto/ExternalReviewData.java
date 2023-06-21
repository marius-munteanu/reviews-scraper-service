package com.scraper.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExternalReviewData {
    @JsonProperty("originalRating")
    private OriginalRatingDto originalRatingDto;
    @JsonProperty("revisedRating")
    private RevisedRatingDto revisedRatingDto;
    private String pdId;
}

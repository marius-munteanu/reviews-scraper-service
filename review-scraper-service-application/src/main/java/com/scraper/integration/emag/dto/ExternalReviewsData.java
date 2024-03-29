package com.scraper.integration.emag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalReviewsData {

    @JsonProperty("reviews")
    private ExternalProductReviewsData externalProductReviewsData;
}

package com.scraper.app.dto;


import lombok.Builder;

@Builder
public final class CreatedOnReviews {
    private final String createdOn;
    private final int numberOfReviews;
}

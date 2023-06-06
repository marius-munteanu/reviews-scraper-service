package com.scraper.app.dto;

public record ExternalReviewsDTO(double originalRating, double revisedRating, String pdId, int count) {
}
package com.scraper.integration.emag.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductReviewsDto {
    private String pdId;
    private List<ReviewDto> review;
}

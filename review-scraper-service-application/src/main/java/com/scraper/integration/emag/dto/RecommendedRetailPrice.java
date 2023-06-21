package com.scraper.integration.emag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecommendedRetailPrice {
    public int amount;
    @JsonProperty("is_visible")
    public boolean is_visible;
}
package com.scraper.integration.emag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RatingDistribution{
    @JsonProperty("1")
    public int _1;
    @JsonProperty("2")
    public int _2;
    @JsonProperty("3")
    public int _3;
    @JsonProperty("4")
    public int _4;
    @JsonProperty("5")
    public int _5;
}
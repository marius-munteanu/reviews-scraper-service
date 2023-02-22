package com.scraper.integration.emag.dto.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalProductReviewsData {

    @JsonProperty("count")
    public int count;
    public FirstItem first_item;
    public ArrayList<Item> items;
    public RatingDistribution rating_distribution;
    public int positive_rating_percentage;
    public int bought_count;
    public String bought_count_filter;
    public String bought_count_message;
    public String positive_rating_percentage_message;
}

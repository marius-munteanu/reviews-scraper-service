package com.scraper.app.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class ReviewsMetaInfo {
    private Map<String, WordCountInfo> wordCountInfo;
    private Map<String, Integer> repetitiveTitleNames;
}

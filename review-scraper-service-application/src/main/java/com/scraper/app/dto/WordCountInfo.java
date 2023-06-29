package com.scraper.app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class WordCountInfo {
    private int count;
    private BigDecimal percentageOfTotal;
}

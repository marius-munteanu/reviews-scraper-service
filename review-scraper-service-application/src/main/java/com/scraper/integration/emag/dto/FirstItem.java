package com.scraper.integration.emag.dto;

import lombok.Data;

@Data
public class FirstItem {
    public Product product;
    public long id;
    public boolean is_bought;
}
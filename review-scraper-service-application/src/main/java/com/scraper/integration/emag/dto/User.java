package com.scraper.integration.emag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
    private int id;
    private String hash;
    private String name;
    private String nickname;
    @JsonProperty("is_official")
    private boolean is_official;
}
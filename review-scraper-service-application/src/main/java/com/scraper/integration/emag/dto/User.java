package com.scraper.integration.emag.dto;

import lombok.Data;

@Data
public class User{
    private int id;
    private String hash;
    private String name;
    private String nickname;
    private boolean is_official;
}
package com.scraper.integration.emag.dto;

import lombok.Data;

@Data
public class User{
    public int id;
    public String hash;
    public String name;
    public String nickname;
    public boolean is_official;
}
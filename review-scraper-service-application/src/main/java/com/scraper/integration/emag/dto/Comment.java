package com.scraper.integration.emag.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Comment{
    public int parent_id;
    public boolean is_official;
    public String type;
    public Product product;
    public String content_no_tags;
    public String moderated_by;
    public int id;
    public String content;
    public User user;
    public boolean is_active;
    public String moderation_status;
    public Date created;
    public Date modified;
    public Date published;
    public Object deleted;
    public String report_reason;
    public String report_reason_description;
}
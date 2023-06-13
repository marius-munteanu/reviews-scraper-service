package com.scraper.integration.emag.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Comment{
    private int parent_id;
    private boolean is_official;
    private String type;
    private Product product;
    private String content_no_tags;
    private String moderated_by;
    private int id;
    private String content;
    private User user;
    private boolean is_active;
    private String moderation_status;
    private Date created;
    private Date modified;
    private Date published;
    private Object deleted;
    private String report_reason;
    private String report_reason_description;
}
package com.scraper.integration.emag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Comment{
    private int parent_id;
    @JsonProperty("is_official")
    private boolean is_official;
    private String type;
    private Product product;
    private String content_no_tags;
    private String moderated_by;
    private int id;
    private String content;
    private User user;
    @JsonProperty("is_active")
    private boolean is_active;
    private String moderation_status;
    private Date created;
    private Date modified;
    private Date published;
    private String deleted;
    private String report_reason;
    private String report_reason_description;
}
package com.scraper.integration.emag.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class Item {
    private int rating;
    private boolean is_bought;
    private int votes;
    private ArrayList<Comment> comments = new ArrayList<>();
    private boolean current_customer_has_voted;
    private int brand_id;
    private int category_id;
    private int offer_id;
    private String client_type;
    private String client_type_info;
    private String type;
    private String title;
//    private Product product;
    private String content_no_tags;
    private String moderated_by;
    private long id;
    private String content;
    private User user;
    private boolean is_active;
    private String moderation_status;
    private Date created;
    private Date modified;
    private Date published;
    private Date deleted;
    private String report_reason;
    private String report_reason_description;
}
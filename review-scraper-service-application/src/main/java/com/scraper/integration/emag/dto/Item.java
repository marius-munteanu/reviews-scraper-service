package com.scraper.integration.emag.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class Item {
    public int rating;
    public boolean is_bought;
    public int votes;
    public ArrayList<Comment> comments = new ArrayList<>();
    public boolean current_customer_has_voted;
    public int brand_id;
    public int category_id;
    public int offer_id;
    public String client_type;
    public String client_type_info;
    public String type;
    public String title;
//    public Product product;
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
    public Date deleted;
    public String report_reason;
    public String report_reason_description;
}
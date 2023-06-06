package com.scraper.integration.emag.dto.review;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OriginalReviewerDto {
    private long id;
    private String userHash;
    private String name;
    private String nickName;
}

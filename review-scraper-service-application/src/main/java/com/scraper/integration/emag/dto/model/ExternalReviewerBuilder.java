package com.scraper.integration.emag.dto.model;

import com.scraper.data.model.ExternalReviewer;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExternalReviewerBuilder {
    private String name;
    private String externalUserId;
    private String nickname;
    private String createdOn;

    public ExternalReviewer toInternal() {
        ExternalReviewer externalReviewer = new ExternalReviewer();
        externalReviewer.setExternalUserId(this.externalUserId);
        externalReviewer.setName(this.name);
        externalReviewer.setNickname(this.nickname);
        externalReviewer.setCreatedOn(this.createdOn);

        return externalReviewer;
    }
}

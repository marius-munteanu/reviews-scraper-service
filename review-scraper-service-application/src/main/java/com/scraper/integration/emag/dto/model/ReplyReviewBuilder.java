package com.scraper.integration.emag.dto.model;

import com.scraper.data.model.ReplyReview;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReplyReviewBuilder {

    private int reviewExternalId;
    private String comment;
    private ExternalReviewerBuilder externalReviewerBuilder;

    public ReplyReview toInternal() {
        ReplyReview replyReview = new ReplyReview();
        replyReview.setReviewExternalId(this.reviewExternalId);
        replyReview.setComment(this.comment);
        replyReview.setExternalReviewer(this.externalReviewerBuilder.toInternal());

        return replyReview;
    }
}

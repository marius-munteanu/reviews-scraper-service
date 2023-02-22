package com.scraper.integration.emag.dto.model;

import com.scraper.data.model.OriginalReview;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
public class OriginalReviewBuilder {

    private String partId;
    private int reviewExternalId;
    private String review;
    private String title;
    private int likes;
    private boolean verifiedPurchase;
    private String createdOn;
    private String updatedOn;
    private ExternalReviewerBuilder externalReviewerBuilder;
    private Set<ReplyReviewBuilder> replyReviewsBuilder;

    public OriginalReview toInternal() {
        OriginalReview originalReview = new OriginalReview();
        originalReview.setReviewExternalId(this.reviewExternalId);
        originalReview.setReview(this.review);
        originalReview.setTitle(this.title);
        originalReview.setPartId(this.partId);
        originalReview.setLikes(this.likes);
        originalReview.setVerifiedPurchase(this.verifiedPurchase);
        originalReview.setCreatedOn(this.createdOn);
        originalReview.setUpdatedOn(this.updatedOn);

        originalReview.setExternalReviewer(this.externalReviewerBuilder.toInternal());
        originalReview.setReplyReviews(this.replyReviewsBuilder.stream()
                .map(ReplyReviewBuilder::toInternal)
                .collect(Collectors.toSet()));

        return originalReview;
    }
}

package com.scraper.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "OriginalReview")
@Table(name = "originalreview")
@Slf4j
public class OriginalReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_external_id", unique = true)
    private long reviewExternalId;

    @Column(name = "part_id", unique = true)
    private String partId;

    @Column(name = "review", length = 2048)
    private String review;

    @Column(name = "rating")
    private int rating;

    @Column(name = "title", length = 2048)
    private String title;

    @Column(name = "likes")
    private int likes;

    @Column(name = "verified_purchase")
    private boolean verifiedPurchase;

    @Column(name = "created_on")
    private String createdOn;

    @Column(name = "updated_on")
    private String updatedOn;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ReplyReview> replyReviews = new LinkedHashSet<>();

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ExternalReviewer externalReviewer;
}

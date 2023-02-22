package com.scraper.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ReplyReview")
@Table(name = "replyreview")
@Slf4j
public class ReplyReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_external_id")
    private int reviewExternalId;

    @Column(name = "comment", length = 2048)
    private String comment;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ExternalReviewer externalReviewer;
}
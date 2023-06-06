package com.scraper.app.service;

import com.scraper.app.dto.ExternalReviewsDTO;
import com.scraper.data.model.OriginalReview;
import com.scraper.integration.emag.dto.Item;
import com.scraper.integration.emag.dto.review.OriginalReviewDto;
import com.scraper.integration.emag.dto.review.OriginalReviewerDto;
import com.scraper.integration.emag.dto.review.OriginalReviewsDto;
import com.scraper.integration.emag.external.service.EmagService;
import com.scraper.dao.OriginalReviewDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewScraperService {

    private final EmagService emagService;
    private final OriginalReviewDao originalReviewDao;
    public ResponseEntity<ExternalReviewsDTO> scrapeProductReviews(String pdId, String productName) {
        var originalReviews = emagService.getAggregatedReviews(pdId, productName).getExternalProductReviewsData().getItems().stream()
                .map(this::buildOriginalReview)
                .toList();

        OriginalReviewsDto originalReviewsDto = OriginalReviewsDto.builder()
                .originalReviewDtos(originalReviews)
                .pdId(pdId)
                .numberOfReviews(originalReviews.size())
                .build();

        return ResponseEntity.ok(saveReviews(originalReviewsDto));
    }

    private ExternalReviewsDTO saveReviews(OriginalReviewsDto originalReviewsDto) {
        var reviews = originalReviewsDto.getOriginalReviewDtos().stream()
                .map(review -> createOriginalReviewEntry(review, originalReviewsDto.getPdId())).toList();

        var listReviews = originalReviewDao.saveAll(reviews);
        var originalRating = listReviews.stream()
                .mapToInt(OriginalReview::getRating)
                .average()
                .orElse(0.0);

        var revisedRating = 0.0;

        return new ExternalReviewsDTO(originalRating, revisedRating, originalReviewsDto.getPdId(), originalReviewsDto.getNumberOfReviews());
    }

    private OriginalReview createOriginalReviewEntry(OriginalReviewDto review, String pdId) {
        OriginalReview originalReview = new OriginalReview();
        originalReview.setReviewExternalId(review.getReviewId());
        originalReview.setPartId(pdId);
        originalReview.setReview(review.getContent());
        originalReview.setRating(review.getRating());
        originalReview.setTitle(review.getTitle());
        originalReview.setLikes(review.getNumberOfVotes());
        originalReview.setVerifiedPurchase(review.isVerifiedPurchase());
        originalReview.setCreatedOn(review.getCreatedOn().toString());

        return originalReview;
    }

    private OriginalReviewDto buildOriginalReview(Item reviewItem) {
        return OriginalReviewDto.builder()
                .title(reviewItem.getTitle())
                .content(reviewItem.getContent())
                .rating(reviewItem.getRating())
                .reviewId(reviewItem.getId())
                .numberOfVotes(reviewItem.getVotes())
                .createdOn(LocalDateTime.ofInstant(reviewItem.getCreated().toInstant(), ZoneId.systemDefault()))
                .updatedOn(LocalDateTime.now())
                .originalReviewerDto(OriginalReviewerDto.builder()
                        .id(reviewItem.getUser().getId())
                        .name(reviewItem.getUser().getName())
                        .userHash(reviewItem.getUser().getHash())
                        .nickName(reviewItem.getUser().getNickname())
                        .build())
                .replyReviews(reviewItem.getComments().stream().map(reply -> OriginalReviewDto.builder()
                        .reviewId(reply.getId())
                        .content(reply.getContent())
                        .originalReviewerDto(OriginalReviewerDto.builder()
                                .id(reply.getUser().getId())
                                .name(reply.getUser().getName())
                                .userHash(reply.getUser().getHash())
                                .nickName(reply.getUser().getNickname())
                                .build())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}

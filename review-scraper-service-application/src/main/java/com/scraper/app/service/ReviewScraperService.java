package com.scraper.app.service;

import com.scraper.app.dto.ExternalReviewData;
import com.scraper.app.dto.OriginalRatingDto;
import com.scraper.app.dto.RevisedRatingDto;
import com.scraper.app.rating.RevisedRatingService;
import com.scraper.data.model.OriginalReview;
import com.scraper.integration.emag.dto.ExternalProductReviewsData;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewScraperService {

    private final EmagService emagService;
    private final RevisedRatingService revisedRatingService;
    private final OriginalReviewDao originalReviewDao;
    public ResponseEntity<ExternalReviewData> scrapeProductReviews(String pdId, String productName) {
        log.info("Scrape product review {}", pdId);
        var reviewItems = new ExternalProductReviewsData();
        var latestOriginalReview = originalReviewDao.findTop1ByPartIdOrderByCreatedOnDesc(pdId);

        if (latestOriginalReview.isPresent()) {
            reviewItems = emagService.getLatestAgragatedReviews(pdId, productName, latestOriginalReview.get().getReviewExternalId()).getExternalProductReviewsData();
        } else  {
            reviewItems = emagService.getAggreegatedReviews(pdId, productName).getExternalProductReviewsData();
        }

        return saveProductReviews(pdId, reviewItems);
    }

    public ResponseEntity<ExternalReviewData> saveProductReviews(String pdId, ExternalProductReviewsData externalProductReviewsData) {
        if (externalProductReviewsData == null || externalProductReviewsData.getItems() == null) {
            log.info("No reviews were found");
            return ResponseEntity.ok().build();
        } else {
            var originalReviews = externalProductReviewsData.getItems();

            log.info("Save product review {} number of new items {}", pdId, originalReviews.size());

            var originalReviewsDtoList = originalReviews.stream()
                    .map(this::buildOriginalReview)
                    .toList();

            OriginalReviewsDto originalReviewsDto = OriginalReviewsDto.builder()
                    .originalReviewDtos(originalReviewsDtoList)
                    .pdId(pdId)
                    .build();

            return ResponseEntity.ok(saveReviews(originalReviewsDto));
        }
    }

    private ExternalReviewData saveReviews(OriginalReviewsDto originalReviewsDto) {
        var reviews = originalReviewsDto.getOriginalReviewDtos().stream()
                .map(review -> createOriginalReviewEntry(review, originalReviewsDto.getPdId())).toList();

        log.info("listReviews.stream() enter");
        var originalRating = getOriginalRating(reviews);
        var revisedRating = getRevisedRating(reviews);
        log.info("listReviews.stream() exit");

        originalReviewDao.saveAll(reviews);

        return ExternalReviewData.builder()
                .originalRatingDto(originalRating)
                .revisedRatingDto(revisedRating)
                .pdId(originalReviewsDto.getPdId())
                .build();
    }

    private OriginalRatingDto getOriginalRating(List<OriginalReview> listReviews) {
        return OriginalRatingDto.builder()
                .totalReviews(listReviews.size())
                .originalRating(BigDecimal.valueOf(listReviews.stream()
                                .mapToInt(OriginalReview::getRating)
                                .average()
                                .orElse(0.00))
                        .setScale(3, RoundingMode.CEILING))
                .build();
    }

    private RevisedRatingDto getRevisedRating(List<OriginalReview> originalReviews) {
        return revisedRatingService.getRevisedRatingForReviews(originalReviews);
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
                .isVerifiedPurchase(reviewItem.is_bought())
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

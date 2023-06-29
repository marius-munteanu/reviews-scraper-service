package com.scraper.app.service;

import com.scraper.app.dto.ExternalReviewData;
import com.scraper.app.dto.OriginalRatingDto;
import com.scraper.app.dto.RevisedRatingDto;
import com.scraper.app.rating.RevisedRatingService;
import com.scraper.data.model.OriginalReview;
import com.scraper.integration.emag.dto.ExternalProductReviewsData;
import com.scraper.integration.emag.dto.Item;
import com.scraper.integration.emag.external.service.EmagService;
import com.scraper.dao.OriginalReviewDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewScraperService {

    private final EmagService emagService;
    private final RevisedRatingService revisedRatingService;
    private final OriginalReviewDao originalReviewDao;

    public ResponseEntity<ExternalReviewData> scrapeProductReviews(String pdId, String productName) {
        log.info("Scraping product review for product ID: {}", pdId);
        ExternalProductReviewsData reviewItems;
        Optional<OriginalReview> latestOriginalReview = originalReviewDao.findTop1ByPartIdOrderByCreatedOnDesc(pdId);

        if (latestOriginalReview.isPresent()) {
            reviewItems = emagService.getLatestAggregatedReviews(pdId, productName, latestOriginalReview.get().getReviewExternalId())
                    .getExternalProductReviewsData();
            return updateProductReviews(pdId, reviewItems);
        } else {
            reviewItems = emagService.getAggregatedReviews(pdId, productName)
                    .getExternalProductReviewsData();
            return saveProductReviews(pdId, reviewItems);
        }
    }

    private ResponseEntity<ExternalReviewData> updateProductReviews(String pdId, ExternalProductReviewsData externalProductReviewsData) {
        if (externalProductReviewsData == null || externalProductReviewsData.getItems() == null) {
            log.info("No new reviews were found");
            List<OriginalReview> reviews = originalReviewDao.findAllByPartId(pdId).orElse(List.of());
            return ResponseEntity.ok(buildExternalReviewDataResponse(pdId, reviews));
        } else {
            List<Item> newOriginalReviews = externalProductReviewsData.getItems();

            List<OriginalReview> reviewsToBeSaved = newOriginalReviews.stream()
                    .map(review -> createOriginalReviewEntry(review, pdId))
                    .toList();

            log.info("Save product review {} - Number of new items: {}", pdId, reviewsToBeSaved.size());
            originalReviewDao.saveAll(reviewsToBeSaved);

            List<OriginalReview> allReviewsForPartId = originalReviewDao.findAllByPartId(pdId).orElse(List.of());
            return ResponseEntity.ok(buildExternalReviewDataResponse(pdId, allReviewsForPartId));
        }
    }

    public ResponseEntity<ExternalReviewData> saveProductReviews(String pdId, ExternalProductReviewsData externalProductReviewsData) {
        if (externalProductReviewsData == null || externalProductReviewsData.getItems() == null) {
            log.info("No reviews were found");
            return ResponseEntity.ok().build();
        } else {
            List<Item> originalReviews = externalProductReviewsData.getItems();

            List<OriginalReview> reviews = originalReviews.stream()
                    .map(review -> createOriginalReviewEntry(review, pdId))
                    .toList();

            ExternalReviewData externalReviewData = buildExternalReviewDataResponse(pdId, reviews);

            log.info("Save product review {} - Number of items: {}", pdId, originalReviews.size());
            originalReviewDao.saveAll(reviews);

            return ResponseEntity.ok(externalReviewData);
        }
    }

    private ExternalReviewData buildExternalReviewDataResponse(String pdId, List<OriginalReview> reviews) {
        log.info("Building response for product ID: {}", pdId);
        OriginalRatingDto originalRating = getOriginalRating(reviews);
        RevisedRatingDto revisedRating = getRevisedRating(reviews);

        return ExternalReviewData.builder()
                .originalRatingDto(originalRating)
                .revisedRatingDto(revisedRating)
                .pdId(pdId)
                .build();
    }

    private OriginalRatingDto getOriginalRating(List<OriginalReview> listReviews) {
        log.info("Calculating original rating");
        double averageRating = listReviews.stream()
                .mapToInt(OriginalReview::getRating)
                .average()
                .orElse(0.00);
        BigDecimal originalRating = BigDecimal.valueOf(averageRating)
                .setScale(3, RoundingMode.CEILING);

        return OriginalRatingDto.builder()
                .totalReviews(listReviews.size())
                .originalRating(originalRating)
                .build();
    }

    private RevisedRatingDto getRevisedRating(List<OriginalReview> originalReviews) {
        return revisedRatingService.getRevisedRatingForReviews(originalReviews);
    }

    private OriginalReview createOriginalReviewEntry(Item review, String pdId) {
        OriginalReview originalReview = new OriginalReview();
        originalReview.setReviewExternalId(review.getId());
        originalReview.setPartId(pdId);
        originalReview.setReview(review.getContent());
        originalReview.setRating(review.getRating());
        originalReview.setTitle(review.getTitle());
        originalReview.setLikes(review.getVotes());
        originalReview.setVerifiedPurchase(review.is_bought());
        originalReview.setCreatedOn(review.getCreated().toString());

        return originalReview;
    }
}
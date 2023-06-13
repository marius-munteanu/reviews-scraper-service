package com.scraper.app.service;

import com.scraper.app.dto.ExternalReviewsDTO;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewScraperService {

    private final EmagService emagService;
    private final OriginalReviewDao originalReviewDao;
    public ResponseEntity<ExternalReviewsDTO> scrapeProductReviews(String pdId, String productName) {
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

    public ResponseEntity<ExternalReviewsDTO> updateProductReviews(String pdId, List<OriginalReviewDto> originalReviewDtoList) {
        log.info("Update product review {} number of new items {}", pdId, originalReviewDtoList.size());

        OriginalReviewsDto originalReviewsDto = OriginalReviewsDto.builder()
                .originalReviewDtos(originalReviewDtoList)
                .pdId(pdId)
                .numberOfReviews(originalReviewDtoList.size())
                .build();

        var listReviews = originalReviewsDto.getOriginalReviewDtos().stream()
                .map(review -> createOriginalReviewEntry(review, originalReviewsDto.getPdId())).toList();

        var originalRating = getOriginalRating(listReviews);
        var revisedRating = getRevisedRating(listReviews);

        return ResponseEntity.ok(new ExternalReviewsDTO(originalRating, revisedRating, originalReviewsDto.getPdId(), originalReviewsDto.getNumberOfReviews()));
    }

    public ResponseEntity<ExternalReviewsDTO> saveProductReviews(String pdId, ExternalProductReviewsData externalProductReviewsData) {
        if (externalProductReviewsData == null || externalProductReviewsData.getItems() == null) {
            log.info("No reviews were found");
        } else {
            var originalReviews = externalProductReviewsData.getItems();

            log.info("Save product review {} number of new items {}", pdId, originalReviews.size());

            var originalReviewsDtoList = originalReviews.stream()
                    .map(this::buildOriginalReview)
                    .toList();

            OriginalReviewsDto originalReviewsDto = OriginalReviewsDto.builder()
                    .originalReviewDtos(originalReviewsDtoList)
                    .pdId(pdId)
                    .numberOfReviews(originalReviews.size())
                    .build();

            return ResponseEntity.ok(saveReviews(originalReviewsDto));
        }

        return ResponseEntity.notFound().build();
    }

    private ExternalReviewsDTO saveReviews(OriginalReviewsDto originalReviewsDto) {
        var reviews = originalReviewsDto.getOriginalReviewDtos().stream()
                .map(review -> createOriginalReviewEntry(review, originalReviewsDto.getPdId())).toList();

        var listReviews = originalReviewDao.saveAll(reviews);
        log.info("listReviews.stream() enter");
        var originalRating = getOriginalRating(listReviews);
        var revisedRating = getRevisedRating(listReviews);
        log.info("listReviews.stream() exit");

        return new ExternalReviewsDTO(originalRating, revisedRating, originalReviewsDto.getPdId(), originalReviewsDto.getNumberOfReviews());
    }

    private double getOriginalRating(List<OriginalReview> listReviews) {
        return listReviews.stream()
                .mapToInt(OriginalReview::getRating)
                .average()
                .orElse(0.0);
    }

    private double getRevisedRating(List<OriginalReview> listReviews) {
        return 0.0;
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

package com.scraper.integration.emag.service;

import com.scraper.integration.emag.dto.list.ExternalReviewsData;
import com.scraper.integration.emag.dto.model.ExternalReviewerBuilder;
import com.scraper.integration.emag.dto.model.OriginalReviewBuilder;
import com.scraper.integration.emag.dto.model.ReplyReviewBuilder;
import com.scraper.integration.emag.external.service.EmagService;
import com.scraper.dao.OriginalReviewDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.scraper.integration.emag.util.EmagUtil.convertToProductDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewScraperService {

    private final EmagService emagService;
    private final OriginalReviewDao originalReviewDao;
    public ResponseEntity<ExternalReviewsData> scrapeProductReviews(String productPage) {
        if (!productPage.contains("/pd/")) {
            log.error("No product with that name");
        }
        var partId = productPage.split("/pd/")[1].split("/")[0];

        var productDto = convertToProductDto(productPage);
        var reviews = emagService.getExternalReviewData(productDto);
        getReviewsAndSave(reviews, partId);

        return null;
    }

    private void getReviewsAndSave(List<ExternalReviewsData> originalReviews, String partId) {
        // TODO: get first review

        originalReviews.forEach(externalReviewsData -> externalReviewsData.getExternalProductReviewsData().getItems().stream()
                .map(s -> OriginalReviewBuilder.builder()
                        .partId(partId)
                        .reviewExternalId(s.id)
                        .likes(s.votes)
                        .review(s.content)
                        .title(s.title)
                        .createdOn(s.published.toString())
                        .updatedOn(new Date().toString())
                        .verifiedPurchase(s.is_bought)
                        .externalReviewerBuilder(ExternalReviewerBuilder.builder()
                                .externalUserId(s.user.hash)
                                .name(s.user.name)
                                .nickname(s.user.nickname)
                                .build())
                        .replyReviewsBuilder(s.comments.stream()
                                .map(reply -> ReplyReviewBuilder.builder()
                                        .reviewExternalId(reply.id)
                                        .comment(reply.content)
                                        .externalReviewerBuilder(ExternalReviewerBuilder.builder()
                                                .externalUserId(reply.user.hash)
                                                .name(reply.user.name)
                                                .nickname(reply.user.nickname)
                                                .build())
                                        .build())
                                .collect(Collectors.toSet()))
                        .build())
                .toList()
                .forEach(originalReviewsBuilder -> originalReviewDao.save(originalReviewsBuilder.toInternal())));
    }
}

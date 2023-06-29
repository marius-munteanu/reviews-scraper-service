package com.scraper.integration.emag.external.service;

import com.scraper.integration.emag.dto.ExternalReviewsData;
import com.scraper.app.util.ExternalService;
import com.scraper.integration.emag.dto.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
public class EmagService extends ExternalService {

    private static final String FEEDBACK_REVIEW_LIST = "product-feedback/{productName}/pd/{partNumberId}/reviews/list";
    private static final int PAGE_LIMIT = 10;

    @Autowired
    public EmagService(RestTemplate restTemplate, @Value("${endpoint.emag}") String emagEndpoint) {
        super(restTemplate, emagEndpoint);
    }

    private ExternalReviewsData getExternalReviewData(String pdId, String productName, int offset) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user   -agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        final var httpEntity = new HttpEntity<>(null, headers);

        log.info("get reviews for {} offset {}", productName, offset);
        return getRestTemplate()
                .exchange(getExternalUriBuilder(FEEDBACK_REVIEW_LIST)
                                .queryParam("source_id", "7")
                                .queryParam("page[limit]", PAGE_LIMIT)
                                .queryParam("page[offset]", offset)
                                .queryParam("sort[created]", "desc")
                                .buildAndExpand(productName, pdId)
                                .toUri(),
                        HttpMethod.GET, httpEntity, ExternalReviewsData.class)
                .getBody();
    }

    public ExternalReviewsData getLatestAggregatedReviews(String pdId, String productName, long latestOriginalReviewId) {
        ExternalReviewsData externalReviewsData = new ExternalReviewsData();
        ArrayList<Item> items = new ArrayList<>();
        int offset = 0;
        boolean hasNextPage = true;

        while (hasNextPage) {
            externalReviewsData = getExternalReviewData(pdId, productName, offset);
            if (externalReviewsData.getExternalProductReviewsData() != null) {
                var reviewItems = externalReviewsData.getExternalProductReviewsData().getItems();
                var index = IntStream.range(0, reviewItems.size())
                        .filter(i -> reviewItems.get(i).getId() == latestOriginalReviewId)
                        .findFirst()
                        .orElse(-1);

                // If list is empty and index 0 means that no new reviews
                if (items.isEmpty() && index == 0) {
                    return new ExternalReviewsData();
                } else if (index == -1) {
                    items.addAll(reviewItems);
                    offset += PAGE_LIMIT;
                    hasNextPage = reviewItems.size() == PAGE_LIMIT;
                } else {
                    items.addAll(reviewItems.stream().limit(index).toList());
                    hasNextPage = false;
                }
            }
        }
        externalReviewsData.getExternalProductReviewsData().setItems(items);

        return externalReviewsData;
    }

    public ExternalReviewsData getAggregatedReviews(String pdId, String productName) {
        ExternalReviewsData externalReviewsData = new ExternalReviewsData();
        ArrayList<Item> items = new ArrayList<>();
        int offset = 0;
        boolean hasNextPage = true;

        while (hasNextPage) {
            externalReviewsData = getExternalReviewData(pdId, productName, offset);
            if (externalReviewsData.getExternalProductReviewsData() != null) {
                List<Item> responseData = externalReviewsData.getExternalProductReviewsData().getItems();
                items.addAll(responseData);
                offset += PAGE_LIMIT;
                hasNextPage = responseData.size() == PAGE_LIMIT;
            }
        }

        externalReviewsData.getExternalProductReviewsData().setItems(items);

        return externalReviewsData;
    }
}
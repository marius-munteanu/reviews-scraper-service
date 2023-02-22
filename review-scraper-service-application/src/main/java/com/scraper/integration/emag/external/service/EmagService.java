package com.scraper.integration.emag.external.service;

import com.scraper.integration.emag.dto.ProductDto;
import com.scraper.integration.emag.dto.list.ExternalReviewsData;
import com.scraper.integration.emag.util.ExternalService;
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

@Service
@Slf4j
public class EmagService extends ExternalService {

    private static final String FEEDBACK_REVIEW_LIST = "product-feedback/{productName}/pd/{partNumberId}/reviews/list";

    @Autowired
    public EmagService(RestTemplate restTemplate, @Value("${endpoint.emag}") String emagEndpoint) {
        super(restTemplate, emagEndpoint);
    }

    public List<ExternalReviewsData> getExternalReviewData(ProductDto productDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user   -agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

        final var httpEntity = new HttpEntity<>(null, headers);
        List<ExternalReviewsData> externalReviewsDataList = new ArrayList<>();

        var s = getReviewData(httpEntity, productDto.getProductName(), productDto.getPdId(), 0, 10, externalReviewsDataList);

        return externalReviewsDataList;
    }

    private List<ExternalReviewsData> getReviewData(HttpEntity httpEntity, String productName, String pdId, int offset, int limit, List<ExternalReviewsData> externalReviewsDataList) {
        log.info("get reviews for " + productName + " limit " + limit + " offset " + offset);
        // add retriable mechanism to change headers freaquently
        ExternalReviewsData externalReviewsData = getRestTemplate()
                .exchange(getExternalUriBuilder(FEEDBACK_REVIEW_LIST)
                                .queryParam("page[limit]", limit)
                                .queryParam("page[offset]", offset)
                                .buildAndExpand(productName, pdId)
                                .toUri(),
                        HttpMethod.GET, httpEntity, ExternalReviewsData.class)
                .getBody();

        if (externalReviewsData != null) {
            externalReviewsDataList.add(externalReviewsData);
            if (externalReviewsData.getExternalProductReviewsData().getItems().size() == limit) {
                getReviewData(httpEntity, productName, pdId, offset + 10, limit, externalReviewsDataList);
            }
        }
        return externalReviewsDataList;
    }
}
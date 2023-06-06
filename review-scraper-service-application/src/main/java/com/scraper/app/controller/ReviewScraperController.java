package com.scraper.app.controller;

import com.scraper.app.dto.ExternalReviewsDTO;
import com.scraper.integration.emag.dto.ExternalReviewsData;
import com.scraper.app.service.ReviewScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/rest/external/reviews")
@RequiredArgsConstructor
public class ReviewScraperController {

    private final ReviewScraperService reviewScraperService;

    @GetMapping(path = "/scrape")
    @ResponseStatus(CREATED)
    public ResponseEntity<ExternalReviewsDTO> scrapeReviews(
            @RequestParam(value="pdId") String pdId,
            @RequestParam(value="productName") String productName) {

        return reviewScraperService.scrapeProductReviews(pdId, productName);
    }

    @GetMapping("/test")
    @ResponseStatus(CREATED)
    public ResponseEntity<ExternalReviewsData> testHirarchy() {

//        reviewScraperService.generateTestData();

        return ResponseEntity.ok().build();
    }

}
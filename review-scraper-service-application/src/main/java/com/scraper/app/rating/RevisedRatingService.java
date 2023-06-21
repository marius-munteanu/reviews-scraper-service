package com.scraper.app.rating;

import com.scraper.app.dto.RevisedRatingDto;
import com.scraper.data.model.OriginalReview;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RevisedRatingService {

    public RevisedRatingDto getRevisedRatingForReviews(List<OriginalReview> originalReviews) {
        int remainingReviews = originalReviews.size();

        var numberOfReviewsPerEachDay = getNumberOfReviewsPerEachDay(originalReviews);
        var percentageOfUnverifiedReviews = calculatePercentageOfUnverifiedReviews(originalReviews, remainingReviews);


        var calculatedRevisedRating = calculateRevisedRating(originalReviews);

        return RevisedRatingDto.builder()
                .reviewsCreatedOnTheSameDay(numberOfReviewsPerEachDay)
                .revisedRating(calculatedRevisedRating)
                .totalReviews(originalReviews.size())
                .remainingReviews(remainingReviews)
                .percentageOfUnVerifiedReviews(percentageOfUnverifiedReviews)
                .build();
    }

    private static BigDecimal calculatePercentageOfUnverifiedReviews(List<OriginalReview> originalReviews, int remainingReviews) {
        var unverifiedReviews = originalReviews.stream()
                .filter(OriginalReview::isVerifiedPurchase)
                .toList()
                .size();

        remainingReviews = remainingReviews - unverifiedReviews;

        double percentage = Math.round(unverifiedReviews * 100d / originalReviews.size() * 100.0);

        return new BigDecimal(percentage);
    }

    private BigDecimal calculateRevisedRating(List<OriginalReview> originalReviews) {
        return new BigDecimal("0.000");
    }

    public static Map<String, Integer> getNumberOfReviewsPerEachDay(List<OriginalReview> originalReviews) {
        return originalReviews.stream()
                .map(OriginalReview::getCreatedOn)
                .map(createdOn -> createdOn.substring(0, createdOn.indexOf("T")))
                .collect(Collectors.groupingBy(date -> date, Collectors.summingInt(date -> 1)))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 9)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}

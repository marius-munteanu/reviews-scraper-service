package com.scraper.app.rating;

import com.scraper.app.dto.ReviewsMetaInfo;
import com.scraper.app.dto.RevisedRatingDto;
import com.scraper.app.dto.WordCountInfo;
import com.scraper.data.model.OriginalReview;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RevisedRatingService {

    public RevisedRatingDto getRevisedRatingForReviews(List<OriginalReview> originalReviews) {
        log.info("Calculate revised rating information");

        var numberOfReviewsPerEachDay = getNumberOfReviewsPerEachDay(originalReviews);
        var unverifiedReviews = numberOfUnverifiedReviews(originalReviews);

        var reviewsMetaInfo = getReviewsMetaInfo(originalReviews);
        var remainingReviews = originalReviews.size() - unverifiedReviews.size();

        var calculatedRevisedRating = calculateRevisedRating(unverifiedReviews);

        return RevisedRatingDto.builder()
                .reviewsCreatedOnTheSameDay(numberOfReviewsPerEachDay)
                .revisedRating(calculatedRevisedRating)
                .totalReviews(originalReviews.size())
                .remainingReviews(remainingReviews)
                .unverifiedReviews(unverifiedReviews.size())
                .reviewsMetaInfo(reviewsMetaInfo)
                .build();
    }

    private ReviewsMetaInfo getReviewsMetaInfo(List<OriginalReview> originalReviews) {

        var wordCountInfo = getWordCountInfo(originalReviews);
        var repetitiveTitleNames = getRepetitiveTitleNames(originalReviews);

        return ReviewsMetaInfo.builder()
                .wordCountInfo(wordCountInfo)
                .repetitiveTitleNames(repetitiveTitleNames)
                .build();
    }

    private Map<String, Integer> getRepetitiveTitleNames(List<OriginalReview> originalReviews) {
        // Step 1: Create the HashMap to store repetitive title names and occurrence counts
        Map<String, Integer> repetitiveTitlesMap = new HashMap<>();

        // Step 2-8: Iterate over originalReviews and populate the HashMap using streams
        originalReviews.stream()
                .map(OriginalReview::getTitle)
                .map(String::trim)
                .filter(title -> !title.isEmpty())
                .forEach(title -> repetitiveTitlesMap.put(title, repetitiveTitlesMap.getOrDefault(title, 0) + 1));

        return repetitiveTitlesMap;
    }

    private Map<String, WordCountInfo> getWordCountInfo(List<OriginalReview> originalReviews) {
        String[] countNumbers = {"0-5", "6-10", "11-15", "16-20", "21-30", "31-50", "51-100", "101-200", "201-500", "501-1000"};

        // Step 1: Create the HashMap to store word count information
        Map<String, WordCountInfo> wordCountMap = new HashMap<>();

        // Step 2-7: Update word count information in the HashMap using streams
        originalReviews.stream()
                .map(OriginalReview::getReview)
                .map(reviewText -> reviewText.split("\\s+"))
                .map(words -> words.length)
                .map(wordCount -> getCountNumberCategory(wordCount, countNumbers))
                .forEach(countNumberCategory -> {
                    WordCountInfo wordCountInfo = wordCountMap.getOrDefault(countNumberCategory, WordCountInfo.builder().build());
                    wordCountInfo.setCount(wordCountInfo.getCount() + 1);
                    wordCountMap.put(countNumberCategory, wordCountInfo);
                });

        // Step 8-13: Calculate percentage of total and return the populated HashMap using streams
        int totalReviews = originalReviews.size();
        Arrays.stream(countNumbers)
                .forEach(countNumberCategory -> {
                    WordCountInfo wordCountInfo = wordCountMap.getOrDefault(countNumberCategory, WordCountInfo.builder().build());
                    BigDecimal percentageOfTotal = BigDecimal.valueOf((double) wordCountInfo.getCount() / totalReviews * 100).setScale(3, RoundingMode.CEILING);
                    wordCountInfo.setPercentageOfTotal(percentageOfTotal);
                    wordCountMap.put(countNumberCategory, wordCountInfo);
                });

        return wordCountMap;

    }

    private String getCountNumberCategory(int wordCount, String[] countNumbers) {
        return Arrays.stream(countNumbers)
                .filter(countNumber -> {
                    String[] range = countNumber.split("-");
                    int min = Integer.parseInt(range[0]);
                    int max = range[1].equals("+") ? Integer.MAX_VALUE : Integer.parseInt(range[1]);
                    return wordCount >= min && wordCount <= max;
                })
                .findFirst()
                .orElse("");
    }

    private static List<OriginalReview> numberOfUnverifiedReviews(List<OriginalReview> originalReviews) {
        return originalReviews.stream()
                .filter(review -> !review.isVerifiedPurchase())
                .toList();
    }

    private BigDecimal calculateRevisedRating(List<OriginalReview> remainingReviews) {
        return BigDecimal.valueOf(remainingReviews.stream()
                        .mapToInt(OriginalReview::getRating)
                        .average()
                        .orElse(0.000))
                .setScale(3, RoundingMode.CEILING);
    }

    private static Map<String, Integer> getNumberOfReviewsPerEachDay(List<OriginalReview> originalReviews) {
        return originalReviews.stream()
                .map(OriginalReview::getCreatedOn)
                .map(createdOn -> createdOn.substring(0, createdOn.indexOf("T")))
                .collect(Collectors.groupingBy(date -> date, Collectors.summingInt(date -> 1)))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 9)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}

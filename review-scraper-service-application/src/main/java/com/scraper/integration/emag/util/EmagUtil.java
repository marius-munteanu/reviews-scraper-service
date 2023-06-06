package com.scraper.integration.emag.util;

public class EmagUtil {

    public static final String FEEDBACK_REVIEWS_INDEX = "https://www.emag.ro/sitemaps/feedback-reviews-index.xml";
    public static final String EMAG_HTTPS = "https://www.emag.ro";
    public static final String EMAG_FEEDBACK = "/product-feedback";
    public static final String EMAG_PD = "/pd";
    public static final String EMAG_REVIEW = "/review";

    public static final String EMAG_REVIEW_URL = "https://www.emag.ro/product-feedback/%s/pd/%s/review/%s";

    public static String urlBuilder(String productName, String pdId, String reviewId) {
        return String.format(EMAG_REVIEW_URL, productName, pdId, reviewId);
    }
}

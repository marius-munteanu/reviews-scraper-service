package com.scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.scraper"})
@EntityScan(basePackages = {"com.scraper"})
public class ReviewScraperApplication {
    private static final String APPLICATION_NAME = "review-scraper";

    public static void main (String[] args) {
        SpringApplication.run(ReviewScraperApplication.class, args);
    }

    public static String getName() {
        return APPLICATION_NAME;
    }
}

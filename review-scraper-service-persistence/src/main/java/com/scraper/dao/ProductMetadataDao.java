package com.scraper.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.scraper.data.model.ProductMetadata;

public interface ProductMetadataDao extends JpaRepository<ProductMetadata, Long> {
}

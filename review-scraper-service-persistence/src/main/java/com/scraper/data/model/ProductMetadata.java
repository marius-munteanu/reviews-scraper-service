package com.scraper.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ProductMetadata")
@Table(name = "productmetadata")
@Slf4j
public class ProductMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "part_id", length = 2048)
    private String partId;

    @Column(name = "long_name")
    private String longName;

    @Column(name = "added_on")
    private String addedOn;
}

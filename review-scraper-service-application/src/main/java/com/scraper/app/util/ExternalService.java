package com.scraper.app.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
@Getter
@Slf4j
public class ExternalService {

    private final RestTemplate restTemplate;
    private final String baseHttpUrl;

    protected URI getExternalUri(String path, Object... uriVariableValues) {
        return getExternalUriBuilder(path).buildAndExpand(uriVariableValues).toUri();
    }

    protected URI getExternalUri(String path) {
        return getExternalUriBuilder(path).buildAndExpand().toUri();
    }

    protected HttpHeaders getJsonHeaders() {
        return getHttpHeaders(MediaType.APPLICATION_JSON);
    }

    private HttpHeaders getHttpHeaders(MediaType mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);
        return httpHeaders;
    }

    protected UriComponentsBuilder getExternalUriBuilder(String path) {
        return getBaseUriBuilder().path(path);
    }

    protected UriComponentsBuilder getBaseUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl(baseHttpUrl);
    }
}

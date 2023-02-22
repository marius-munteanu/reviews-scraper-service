package com.scraper.common.config;

import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.client.RestTemplateSpanDecorator;
import io.opentracing.contrib.spring.web.client.TracingRestTemplateInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@Slf4j
public class RestTemplateConfiguration {

    @Value("${httppool.maxTotal}")
    private int maxTotal;
    @Value("${httppool.maxPerRoute}")
    private int maxPerRoute;

    @Value("${httppool.connectionRequestTimeout}")
    private int connectionRequestTimeout;

    @Value("${httppool.socketTimeout}")
    private int socketTimeout;

    @Value("${httppool.connectionTimeout}")
    private int connectTimeout;

    @Bean
    @ConditionalOnMissingBean
    CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setCookieSpec(CookieSpecs.STANDARD)
                .setConnectTimeout(connectTimeout)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }

    @Bean
    public RestTemplate restTemplate(Tracer tracer, RestTemplateSpanDecorator restTemplateSpanDecorator) {
        log.info("RestTemplate Bean init. Tracer : {}; Span decorator {}", tracer, restTemplateSpanDecorator);
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new
                HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClient());

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        restTemplate.setInterceptors(List.of(new TracingRestTemplateInterceptor(tracer,
                List.of(new RestTemplateSpanDecorator.StandardTags(), restTemplateSpanDecorator)),
                new ServiceNameHeaderInterceptor()));

        return restTemplate;
    }
}

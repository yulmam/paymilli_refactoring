package com.paymilli.paymilli.global.config;


import com.paymilli.paymilli.global.filter.ClientErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final String BASE_URL = "https://j11a702.p.ssafy.io/api/v1/cardcompany";

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.
            baseUrl(BASE_URL)
            .filter(new ClientErrorHandler())
            .build();
    }

}

package com.robomatic.core.v1.configurations;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateConfig {

    private final GcpAuthInterceptor gcpAuthInterceptor;

    public RestTemplateConfig(GcpAuthInterceptor gcpAuthInterceptor) {
        this.gcpAuthInterceptor = gcpAuthInterceptor;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors(gcpAuthInterceptor)
                .build();
    }
}

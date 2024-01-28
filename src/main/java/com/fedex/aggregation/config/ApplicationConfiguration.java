package com.fedex.aggregation.config;

import feign.Retryer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }
}

package com.dolloer.million.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class AppConfig {

    @Bean
    public AtomicInteger searchCount() {
        return new AtomicInteger(0);
    }
}

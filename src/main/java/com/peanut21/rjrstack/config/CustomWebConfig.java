package com.peanut21.rjrstack.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CustomWebConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

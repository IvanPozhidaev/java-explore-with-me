package ru.practicum.ewm.stats;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EwmStatsClientConfig {

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }
}
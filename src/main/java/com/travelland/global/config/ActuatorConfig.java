package com.travelland.global.config;

import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    @Bean
    public InMemoryHttpExchangeRepository httpExchangeRepository() {
        InMemoryHttpExchangeRepository exchangeRepository = new InMemoryHttpExchangeRepository();
        exchangeRepository.setCapacity(50); //저장할 최대 HTTP 요청 수

        return exchangeRepository;
    }
}

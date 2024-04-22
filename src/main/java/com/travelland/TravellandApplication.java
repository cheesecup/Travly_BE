package com.travelland;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing // for 생성일&수정일
@SpringBootApplication
@EnableScheduling
//@OpenAPIDefinition(servers = {@Server(url = "https://spparta.store", description = "Default Server URL")})
public class TravellandApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravellandApplication.class, args);
    }

}

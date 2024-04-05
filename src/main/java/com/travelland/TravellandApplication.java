package com.travelland;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // for 생성일&수정일
@SpringBootApplication
//@OpenAPIDefinition(servers = {@Server(url = "https://spparta.store", description = "Default Server URL")})

public class TravellandApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravellandApplication.class, args);
    }

}

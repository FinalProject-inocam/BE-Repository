package com.example.finalproject;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(servers = {@Server(url = "https://innomotors.store/", description = "Default Server URL")})

@EnableJpaAuditing
@SpringBootApplication
public class FinalprojectApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinalprojectApplication.class, args);
    }

}

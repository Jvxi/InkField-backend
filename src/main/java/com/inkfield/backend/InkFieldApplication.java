package com.inkfield.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.inkfield.backend.config.InkFieldAppProperties;
import com.inkfield.backend.config.InkFieldMailProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({ InkFieldMailProperties.class, InkFieldAppProperties.class })
public class InkFieldApplication {
    public static void main(String[] args) {
        SpringApplication.run(InkFieldApplication.class, args);
    }
}

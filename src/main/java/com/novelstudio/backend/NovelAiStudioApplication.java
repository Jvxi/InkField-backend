package com.novelstudio.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.novelstudio.backend.config.NovelAppProperties;
import com.novelstudio.backend.config.NovelMailProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({ NovelMailProperties.class, NovelAppProperties.class })
public class NovelAiStudioApplication {
    public static void main(String[] args) {
        SpringApplication.run(NovelAiStudioApplication.class, args);
    }
}

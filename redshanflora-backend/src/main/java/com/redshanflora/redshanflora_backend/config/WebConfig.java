package com.redshanflora.redshanflora_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Project root directory
        String projectPath = System.getProperty("user.dir");

        // uploads folder inside the project
        String uploadPath = projectPath + File.separator + "uploads";

        System.out.println("Project Path: " + projectPath);
        System.out.println("Upload Path : " + uploadPath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + File.separator);

        registry.addResourceHandler("/models/**")
                .addResourceLocations("file:" + uploadPath + File.separator + "models" + File.separator);
    }
}
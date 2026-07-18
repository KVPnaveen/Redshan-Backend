package com.redshanflora.redshanflora_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:///C:/Users/Windows/Documents/GitHub/Redshan-Backend/redshanflora-backend/uploads/"
                );

        registry.addResourceHandler("/models/**")
                .addResourceLocations(
                        "file:///C:/Users/Windows/Documents/GitHub/Redshan-Backend/redshanflora-backend/uploads/models/"
                );
    }
}


package com.redshanflora.redshanflora_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = resolveUploadDir();
        String uploadUri = uploadPath.toUri().toString();

        if (!uploadUri.endsWith("/")) {
            uploadUri += "/";
        }

        System.out.println("==================================================");
        System.out.println("RESOLVED UPLOADS PATH: " + uploadPath.toAbsolutePath());
        System.out.println("RESOLVED UPLOADS URI : " + uploadUri);
        System.out.println("==================================================");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadUri);

        registry.addResourceHandler("/models/**")
                .addResourceLocations(uploadUri + "models/");
    }

    private Path resolveUploadDir() {
        String currentDir = System.getProperty("user.dir");

        // Option A: If opened from 'Redshan-Backend' (like your friend)
        Path optionA = Paths.get(currentDir, "redshanflora-backend", "uploads");
        if (optionA.toFile().exists()) {
            return optionA.toAbsolutePath().normalize();
        }

        // Option B: If opened directly from 'redshanflora-backend' (like you)
        Path optionB = Paths.get(currentDir, "uploads");
        if (optionB.toFile().exists()) {
            return optionB.toAbsolutePath().normalize();
        }

        // Option C: If running from parent/sibling subdirectories
        Path optionC = Paths.get(currentDir, "..", "uploads");
        if (optionC.toFile().exists()) {
            return optionC.toAbsolutePath().normalize();
        }

        // Fallback: Create uploads directory in current folder if it doesn't exist
        Path fallback = Paths.get(currentDir, "uploads").toAbsolutePath().normalize();
        fallback.toFile().mkdirs();
        return fallback;
    }
}
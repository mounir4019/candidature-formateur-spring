package com.candidatureformateur.security;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Autoriser les requêtes depuis votre domaine Angular tfh
        registry.addMapping("/**").allowedOrigins("*");
    }
}

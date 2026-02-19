package com.taskmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration for the Task Management API
 *
 * Configures Cross-Origin Resource Sharing (CORS) to allow the frontend
 * application to make requests to the backend API from different origins.
 *
 * This configuration is essential for development (frontend on :4200, backend on :8080)
 * and production deployments where frontend and backend are on different domains.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configure CORS mappings to allow cross-origin requests.
     *
     * Development: Allows localhost:4200 (Angular dev server)
     * Production: Should be configured with actual frontend domain
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Allow requests from Angular development server
                .allowedOrigins(
                        "http://localhost:4200",
                        "http://127.0.0.1:4200"
                        // Add production frontend URL here
                        // "https://yourdomain.com"
                )
                // Allow all standard HTTP methods
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
                // Allow all headers (adjust for production)
                .allowedHeaders("*")
                // Allow credentials (cookies, authorization headers)
                .allowCredentials(true)
                // Cache preflight response for 1 hour
                .maxAge(3600);
    }
}


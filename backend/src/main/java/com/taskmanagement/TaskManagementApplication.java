package com.taskmanagement;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot application entry point for the Task Management API.
 *
 * This application provides a RESTful API for managing tasks with features including:
 * - CRUD operations for tasks
 * - Filtering, sorting, and pagination
 * - Task validation and error handling
 * - API documentation via Swagger/OpenAPI
 */
@SpringBootApplication
public class TaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
    }

    /**
     * Configure OpenAPI documentation for Swagger UI.
     * Provides metadata about the API including title, description, version, and contact information.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Management API")
                        .description("A comprehensive RESTful API for managing tasks with advanced features including filtering, sorting, and pagination.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@taskmanagement.com")
                                .url("https://github.com/"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}


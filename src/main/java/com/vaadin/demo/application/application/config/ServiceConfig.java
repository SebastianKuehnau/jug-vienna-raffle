package com.vaadin.demo.application.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Service configuration to ensure our hexagonal architecture services are discovered
 */
@Configuration
@ComponentScan(basePackages = {
    "com.vaadin.demo.application.domain.port",
    "com.vaadin.demo.application.application.service",
    "com.vaadin.demo.application.adapter",
})
public class ServiceConfig {
    // Configuration class to ensure component scanning picks up our services
}
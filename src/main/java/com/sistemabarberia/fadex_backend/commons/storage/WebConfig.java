package com.sistemabarberia.fadex_backend.commons.storage;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve cualquier URL que empiece con /uploads/ desde la carpeta física "uploads"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
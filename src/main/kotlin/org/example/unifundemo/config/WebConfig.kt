package org.example.unifundemo.config // Make sure this package name is correct

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // Apply to all API endpoints
            .allowedOrigins("http://localhost:5175") // ✅ Allow requests from your frontend
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Allow these HTTP methods
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(true) // Allow sending cookies
    }
}
package org.example.unifundemo.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
// ✅ 1. 클래스 생성자를 통해 'file.upload-dir' 값을 주입받습니다.
class WebConfig(
    @Value("\${file.upload-dir}") private val uploadDir: String
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:5174")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

    // ✅ 2. 주입받은 uploadDir 프로퍼티를 사용하여 이미지 파일 경로를 설정합니다.
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:$uploadDir/")
    }
}
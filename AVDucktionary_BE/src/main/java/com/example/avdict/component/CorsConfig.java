package com.example.avdict.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Cho phép tất cả các đường dẫn
                        .allowedOrigins("*") // Cho phép mọi domain truy cập
                        .allowedMethods("*") // Cho phép tất cả các phương thức HTTP
                        .allowedHeaders("*") // Cho phép tất cả các header
                        .allowCredentials(false); // Không bật credentials (nếu dùng cookies, bật true)
            }
        };
    }
}

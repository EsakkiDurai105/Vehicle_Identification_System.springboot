package com.vehicle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ApiAccessInterceptor apiAccessInterceptor;

    public WebConfig(ApiAccessInterceptor apiAccessInterceptor) {
        this.apiAccessInterceptor = apiAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiAccessInterceptor)
                .addPathPatterns("/api/**", "/h2-console/**");
    }
}
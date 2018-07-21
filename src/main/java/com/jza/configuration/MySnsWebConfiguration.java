package com.jza.configuration;

import com.jza.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Component
public class MySnsWebConfiguration extends WebMvcConfigurationSupport
//        implements WebMvcConfigurer
{

    @Autowired
    PassportInterceptor passportInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(passportInterceptor).addPathPatterns("**").excludePathPatterns("/static/**");
        registry.addInterceptor(passportInterceptor).excludePathPatterns(Arrays.asList("/images/**", "/scripts/**", "/styles/**"));
//        super.addInterceptors(registry);
    }
}

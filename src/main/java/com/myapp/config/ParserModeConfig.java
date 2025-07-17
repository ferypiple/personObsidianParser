package com.myapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserModeConfig {
    @Value("${md.parser.mode:full}")
    private String mode;

    @Bean
    public boolean useMinimalPersonParser() {
        return "minimal".equalsIgnoreCase(mode);
    }
}

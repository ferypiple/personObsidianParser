package com.myapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final Environment env;

    @Bean
    public Path personMdPath() {
        return Paths.get(env.getProperty("md.path.person"));
    }

    @Bean
    public Path counselorMdPath() {
        return Paths.get(env.getProperty("md.path.counselor"));
    }
    }
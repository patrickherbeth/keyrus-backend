package com.example.demo.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfigAccess {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.addAllowedOrigin("*"); // Permite qualquer origem (ajuste conforme necessário)
                    corsConfig.addAllowedMethod("*"); // Permite todos os métodos (GET, POST, etc.)
                    corsConfig.addAllowedHeader("*"); // Permite todos os cabeçalhos
                    return corsConfig;
                }))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permite acesso a todas as rotas sem autenticação
                );

        return http.build();
    }

}


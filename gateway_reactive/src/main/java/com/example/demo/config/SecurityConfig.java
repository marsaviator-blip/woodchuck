package com.example.demo.config;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@Configuration
public class SecurityConfig {

    @Bean
    public ServerAuthenticationConverter wrongcustomHeaderConverter() {
        return (ServerWebExchange exchange) -> {
            String token = exchange.getRequest().getHeaders().getFirst("X-Auth-Token");

            if (token == null || token.isEmpty()) {
                return Mono.empty(); // No token, skip this converter
            }

            // Return unauthenticated token for the ReactiveAuthenticationManager
            return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
        };
    }
}

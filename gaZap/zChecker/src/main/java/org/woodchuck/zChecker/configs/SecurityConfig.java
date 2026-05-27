package org.woodchuck.zChecker.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(form -> form.disable())   // Disable default login page
            .httpBasic(basic -> basic.disable()) // Disable HTTP Basic auth pop-up
            // 1. Tell Spring Security to use the CorsConfigurationSource bean below
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 2. Your other security settings
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Use allowedOriginPatterns instead of allowedOrigins if using credentials
        config.setAllowedOrigins(List.of("https://localhost:3002"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        //config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

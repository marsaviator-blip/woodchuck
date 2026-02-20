package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;



@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
    // Inject your custom ReactiveAuthenticationManager
    private final JWTAuthenticationManager authenticationManager;
    // Inject or define a custom converter to extract credentials from the request
    //private final ServerAuthenticationConverter authenticationConverter;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();    
    //private final ReactiveUserDetailsService reactiveUserDetailsService;    

    public WebSecurityConfig(JWTAuthenticationManager authenticationManager,
                            ServerAuthenticationConverter authenticationConverter) {
        this.authenticationManager = authenticationManager;
        //this.authenticationConverter = authenticationConverter;
        //this.reactiveUserDetailsService = reactiveUserDetailsService;
    }

//     @Bean
//     public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
//         http
//             .authorizeExchange(exchanges -> exchanges
//                 .pathMatchers("/actuator/**").permitAll()
//                 .pathMatchers("/public").permitAll()
//                 .anyExchange().authenticated()
//             )
//             .csrf(csrf -> csrf.disable())
//             .httpBasic(httpBasic -> {}            
//         );
//         return http.build();
//     }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // Create an AuthenticationWebFilter with the custom manager and converter
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(customHeaderConverter());

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for stateless APIs
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/login").permitAll() // Allow public access to login
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/public").permitAll()
                        .anyExchange().authenticated() // Secure all other endpoints
                )
                .authenticationManager(authenticationManager) // Set the custom authentication manager
                // .addBefore(new JwtAuthenticationFilter(reactiveUserDetailsService), SecurityWebFiltersOrder.AUTHENTICATION) // Add custom JWT filter before authentication
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION) // Add the filter to the chain
                .build();
    }

    @Bean
    public ServerAuthenticationConverter customHeaderConverter() {
        return (ServerWebExchange exchange) -> {
            String token = exchange.getRequest().getHeaders().getFirst("X-Auth-Token");

            if (token == null || token.isEmpty()) {
                return Mono.empty(); // No token, skip this converter
            }

            // Return unauthenticated token for the ReactiveAuthenticationManager
            return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
        };
    }
    // You may also need a ReactiveUserDetailsService if using standard user/password flow
    // or a custom ReactiveJwtDecoder if using JWTs as shown in some Spring docs.

}
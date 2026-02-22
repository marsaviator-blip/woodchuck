package com.woodchuck.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;

//import com.example.demo.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) { 
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
            return new CustomAuthenticationToken(userDetails, password, "company", userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
    // @Override
    // public Authentication authenticate(Authentication authentication) {
    //     return userDetailsService.loadUserByUsername(authentication.getName());
    //         //.flatMap(userDetails -> verifyPassword(authentication, userDetails))
    //         //.map(userDetails -> new CustomAuthenticationToken(userDetails));
    // }

    // private UserDetails verifyPassword(Authentication authentication, UserDetails userDetails) {
    //     return userDetails
    //         .filter(u -> passwordEncoder.matches(authentication.getCredentials().toString(), u.getPassword()))
    //         .switchIfEmpty(new RuntimeException("Invalid credentials"));
    // }
//    @Override
//    public Authentication authenticate(Authentication authentication) {
        // Implement your custom authentication logic here.
        // For example, validate a token, look up user details, etc.
        
        // This is a placeholder example for a valid, pre-processed token
//        if (authentication instanceof CustomAuthenticationToken) {
             // If valid, set authenticated to true and return
//             authentication.setAuthenticated(true); 
//             return authentication;
//        }
        
        // Return an error if authentication fails
//        return new BadCredentialsException("Invalid credentials");
//    }
}
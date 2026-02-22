package com.woodchuck.gateway_servlet.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import com.woodchuck.gateway_servlet.service.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@Component
public class JWTAuthenticationManager implements AuthenticationManager {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationManager(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();
        String username = jwtUtil.extractUsername(token);
        User userDetails = (User) userDetailsService.loadUserByUsername(username);
        if (jwtUtil.validateToken(token, userDetails.getUsername())) {
            return authentication;
        } else {
            throw new AuthenticationException("Invalid JWT token") {};
        }
    }   

    // @Override
    // public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    //     String token = authentication.getCredentials().toString();
    //     String username = jwtUtil.extractUsername(token);

    //     return userDetailsService.loadUserByUsername(username)
    //             .map(userDetails -> {
    //                 if (jwtUtil.validateToken(token, userDetails.getUsername())) {
    //                     return authentication;
    //                 } else {
    //                     throw new AuthenticationException("Invalid JWT token") {};
    //                 }
    //             });
    // }

//     public ServerAuthenticationConverter authenticationConverter() {
//         return new ServerAuthenticationConverter() {
// //            @Override
//             public Authentication convert(ServerWebExchange exchange) {
//                 String token = exchange.getRequest().getHeaders().getFirst("Authorization");
//                 if (token != null && token.startsWith("Bearer ")) {
//                     token = token.substring(7);
//                     return SecurityContextHolder.getContext().getAuthentication();
//                 }
//                 return null;
//             }
//         };
//     }
}

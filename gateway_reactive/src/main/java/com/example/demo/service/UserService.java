package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {


    private User user;
    private UserDetails userDetails;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();  
    private final InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager(
                                userDetails = User.withUsername("user")
                                    .password(passwordEncoder.encode("password"))
                                    .roles("USER")
                                    .build()
                            );        

    // @Autowired
    // private UserRepository userRepository;

    public Mono<User> findByUsername(String username) {
        //return userRepository.findByUsername(username);
        return Mono.just(new User(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()));
    }

    // public Mono<User> save(User user) {
    //     user.setPassword(user.getPassword()); // Encrypt password before saving
    //     return userRepository.save(user);
    // }
}

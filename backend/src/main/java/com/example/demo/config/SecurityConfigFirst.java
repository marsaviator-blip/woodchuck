
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfigFirst {
    // @Bean
    // public SecurityWebFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
    //     return http
    //             .authorizeRequests()
    //             .anyRequest().permitAll()
    //             .and()
    //             .csrf().disable()
    //             .build();
    // }
}

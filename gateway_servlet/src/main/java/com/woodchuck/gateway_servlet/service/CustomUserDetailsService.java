


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Inject a repository or other data access component here

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                ClientRegistration keycloak = ClientRegistrations.fromOidcIssuerLocation(
                    "https://accounts.google.com")
                .clientId("google-client-id")
                .clientSecret("google-client-secret") //FIXME
                .build();
        //return new InMemoryClientRegistrationRepository(keycloak);

        // Retrieve user from your database
        // Example:
        // UserEntity userEntity = userRepository.findByUsername(username)
        //     .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Return a UserDetails object (often using Spring Security's built-in User class)
        // return User.withUsername(userEntity.getUsername())
        //            .password(userEntity.getPassword()) // Pass the encoded password
        //            .roles(userEntity.getRoles().toArray(new String[0]))
        //            .build();
        return null; // Replace with actual logic
    }
}

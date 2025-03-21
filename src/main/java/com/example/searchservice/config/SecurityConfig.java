package com.example.searchservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 *
 * <p>Defines the security rules and authentication mechanisms, including in-memory users
 * and password encoding. Uses HTTP Basic and form-based authentication.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * Sets up authorization rules, CSRF settings, and authentication mechanisms.
     *
     * @param http HttpSecurity to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection for stateless API calls
                .authorizeRequests()
                .antMatchers("/api/health").permitAll() // Allow unauthenticated access to health check endpoint
                .antMatchers("/api/search/**").authenticated() // Require authentication for search-related endpoints
                .anyRequest().authenticated() // All other endpoints require authentication
                .and()
                .httpBasic() // Enable HTTP Basic Authentication
                .and()
                .formLogin(); // Enable form-based login for UI access

        return http.build(); // Build and return the configured security filter chain
    }

    /**
     * Defines an in-memory user details service for testing/demo purposes.
     * In production, consider integrating with a persistent user store (e.g., database, LDAP).
     *
     * @return InMemoryUserDetailsManager with predefined users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Define user with role USER
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password")) // Encode password using BCrypt
                .roles("USER")
                .build();

        // Define admin with roles USER and ADMIN
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin")) // Encode password using BCrypt
                .roles("USER", "ADMIN")
                .build();

        // Create an in-memory user store with the defined users
        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * Creates and configures a password encoder using BCrypt.
     * Used to hash and validate user passwords securely.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for strong password hashing
    }
}

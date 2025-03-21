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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * Sets up authorization rules and authentication methods.
     *
     * @param http HttpSecurity to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure security rules for the API
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API endpoints
                .authorizeRequests()
                .antMatchers("/api/health").permitAll() // Allow health endpoint without auth
                .antMatchers("/api/search/**").authenticated() // Require auth for search endpoints
                .anyRequest().authenticated() // All other requests require authentication
                .and()
                .httpBasic() // Enable HTTP Basic authentication
                .and()
                .formLogin(); // Enable form login

        return http.build();
    }

    /**
     * Creates a UserDetailsService for authentication.
     * In a production environment, this would be replaced with a database-backed service.
     *
     * @return UserDetailsService with in-memory users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Create sample users for testing
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("USER", "ADMIN")
                .build();
        
        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * Creates a password encoder for securely storing user passwords.
     * Uses BCrypt hashing algorithm for password security.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
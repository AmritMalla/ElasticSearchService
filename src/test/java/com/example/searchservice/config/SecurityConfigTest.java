package com.example.searchservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link SecurityConfig}.
 * Verifies security filters, authentication setup, in-memory user config, and endpoint access.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test: SecurityConfig bean should be initialized by Spring context.
     */
    @Test
    void securityConfigShouldNotBeNull() {
        assertNotNull(securityConfig, "SecurityConfig should be initialized");
    }

    /**
     * Test: PasswordEncoder bean should be created correctly.
     */
    @Test
    void passwordEncoderShouldBeCreated() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder, "PasswordEncoder should be created");
    }

    /**
     * Test: UserDetailsService bean should be available.
     */
    @Test
    void userDetailsServiceShouldBeCreated() {
        UserDetailsService service = securityConfig.userDetailsService();
        assertNotNull(service, "UserDetailsService should be created");
    }

    /**
     * Test: In-memory users should be correctly configured with expected roles.
     */
    @Test
    void userDetailsShouldHaveExpectedUsers() {
        // Load "user" and verify credentials and roles
        UserDetails user = userDetailsService.loadUserByUsername("user");
        assertNotNull(user, "User should exist");
        assertEquals("user", user.getUsername(), "Username should match");
        assertTrue(user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")), "User should have USER role");

        // Load "admin" and verify credentials and roles
        UserDetails admin = userDetailsService.loadUserByUsername("admin");
        assertNotNull(admin, "Admin should exist");
        assertEquals("admin", admin.getUsername(), "Username should match");
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")), "Admin should have ADMIN role");
    }

    /**
     * Test: Health check endpoint (/api/health) should be publicly accessible.
     */
    @Test
    void healthEndpointShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    /**
     * Test: Search endpoint (/api/search) should require authentication.
     */
    @Test
    void searchEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/search"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test: Valid credentials should result in successful authentication.
     */
    @Test
    void validLoginShouldAuthenticate() throws Exception {
        mockMvc.perform(formLogin().user("user").password("password"))
                .andExpect(authenticated());
    }

    /**
     * Test: Invalid credentials should fail authentication.
     */
    @Test
    void invalidLoginShouldNotAuthenticate() throws Exception {
        mockMvc.perform(formLogin().user("user").password("wrongpassword"))
                .andExpect(unauthenticated());
    }
}

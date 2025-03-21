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

    @Test
    void securityConfigShouldNotBeNull() {
        assertNotNull(securityConfig, "SecurityConfig should be initialized");
    }

    @Test
    void passwordEncoderShouldBeCreated() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder, "PasswordEncoder should be created");
    }

    @Test
    void userDetailsServiceShouldBeCreated() {
        UserDetailsService service = securityConfig.userDetailsService();
        assertNotNull(service, "UserDetailsService should be created");
    }

    @Test
    void userDetailsShouldHaveExpectedUsers() {
        UserDetails user = userDetailsService.loadUserByUsername("user");
        assertNotNull(user, "User should exist");
        assertEquals("user", user.getUsername(), "Username should match");
        assertTrue(user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")), "User should have USER role");

        UserDetails admin = userDetailsService.loadUserByUsername("admin");
        assertNotNull(admin, "Admin should exist");
        assertEquals("admin", admin.getUsername(), "Username should match");
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")), "Admin should have ADMIN role");
    }

    @Test
    void healthEndpointShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    void searchEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/search"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validLoginShouldAuthenticate() throws Exception {
        mockMvc.perform(formLogin().user("user").password("password"))
                .andExpect(authenticated());
    }

    @Test
    void invalidLoginShouldNotAuthenticate() throws Exception {
        mockMvc.perform(formLogin().user("user").password("wrongpassword"))
                .andExpect(unauthenticated());
    }
}
package com.buddy.buddy.auth;

import com.buddy.buddy.auth.DTO.AuthenticationRequest;
import com.buddy.buddy.auth.DTO.AuthenticationResponse;
import com.buddy.buddy.auth.DTO.RegisterRequest;
import com.buddy.buddy.auth.controller.AuthenticationController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("test@test.com", "Password123!");
        AuthenticationResponse mockResponse = new AuthenticationResponse("mockToken");

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    @Test
    void testAuthenticate() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("test@test.com", "Password123!");
        AuthenticationResponse mockResponse = new AuthenticationResponse("mockToken");

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(mockResponse);


        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    @Test
    void testAdminAuthenticate() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("admin@test.com", "AdminPassword123!");
        AuthenticationResponse mockResponse = new AuthenticationResponse("adminMockToken");

        when(authenticationService.adminAuthenticate(any(AuthenticationRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/admin/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("adminMockToken"));
    }


    @Test
    @WithMockUser
    void testAuthenticate1() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("test@test.com", "Password123!");
        AuthenticationResponse mockResponse = new AuthenticationResponse("mockToken");

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"));
    }
}

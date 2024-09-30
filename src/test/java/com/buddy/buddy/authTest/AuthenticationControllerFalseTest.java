package com.buddy.buddy.authTest;

import com.buddy.buddy.auth.AuthenticationService;
import com.buddy.buddy.auth.DTO.AuthenticationRequest;
import com.buddy.buddy.auth.DTO.AuthenticationResponse;
import com.buddy.buddy.auth.DTO.RegisterRequest;
import com.buddy.buddy.auth.JwtUtils;
import com.buddy.buddy.auth.controller.AuthenticationController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerFalseTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;
    private RegisterRequest registerRequest;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test");
        authenticationRequest.setPassword("password");

        authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken("mockToken");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test");
        registerRequest.setPassword("password");
        registerRequest.setEmail("newuser");
    }

    @Test
    void testRegister() throws Exception {
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(authenticationResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}

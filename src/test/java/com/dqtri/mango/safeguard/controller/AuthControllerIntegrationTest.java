/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.controller;


import com.dqtri.mango.safeguard.model.CoreUser;
import com.dqtri.mango.safeguard.model.dto.payload.LoginPayload;
import com.dqtri.mango.safeguard.model.dto.payload.RegisterPayload;
import com.dqtri.mango.safeguard.model.dto.response.ErrorResponse;
import com.dqtri.mango.safeguard.model.dto.response.TokenResponse;
import com.dqtri.mango.safeguard.model.enums.Role;
import com.dqtri.mango.safeguard.repository.UserRepository;
import com.dqtri.mango.safeguard.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@WebMvcTest(controllers = {AuthController.class})
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Nested
    class RegisterIntegrationTest {
        private static final String REGISTER_ROUTE = "/auth/register";

        @Captor
        ArgumentCaptor<CoreUser> userArgumentCaptor;

        @BeforeEach
        public void setup() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
        }

        @Test
        void register_givenUserCredentials_thenSuccess() throws Exception {
            RegisterPayload registerPayload = createRegisterPayload();
            assertOk(registerPayload);
            verify(userRepository).save(userArgumentCaptor.capture());
            CoreUser value = userArgumentCaptor.getValue();
            assertThat(value.getRole()).isEqualTo(Role.SUBMITTER);
            verify(userRepository).save(createCoreUser());
        }

        @Test
        void register_givenEmptyPayload_thenBadRequest() throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            assertBadRequest(registerPayload);
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalidEmailFormat", "invalidEmailFormat@", "@invalidEmailFormat"})
        void register_givenInvalidEmailFormat_thenBadRequest(String invalidEmail) throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail(invalidEmail);
            registerPayload.setPassword("mango");
            //then
            assertBadRequest(registerPayload);
        }

        @Test
        void register_givenNonPassword_thenBadRequest() throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail("newcomer@mango.dqtri.com");
            //then
            assertBadRequest(registerPayload);
        }

        @ParameterizedTest
        @ValueSource(strings = {"st", "", "       ", "more_than_24_characters_too_long_password"})
        void register_givenInvalidPasswordFormat_thenBadRequest(String password) throws Exception {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail("newcomer@mango.dqtri.com");
            registerPayload.setPassword(password);
            //then
            assertBadRequest(registerPayload);
        }


        @Test
        void register_mockExitedEmail_thenThrowConflictException() throws Exception {
            RegisterPayload registerPayload = createRegisterPayload();
            when(userRepository.existsByEmail(anyString())).thenReturn(true);
            mvc.perform(post(REGISTER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(registerPayload)))
                    .andExpect(status().isConflict());
            verify(userRepository, never()).save(userArgumentCaptor.capture());
        }

        private void assertOk(RegisterPayload registerPayload) throws Exception {
            mvc.perform(post(REGISTER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(registerPayload)))
                    .andExpect(status().isOk());
        }

        private void assertBadRequest(RegisterPayload registerPayload) throws Exception {
            mvc.perform(post(REGISTER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(registerPayload)))
                    .andExpect(status().isBadRequest());
            verify(userRepository, never()).save(userArgumentCaptor.capture());
        }

        private RegisterPayload createRegisterPayload() {
            RegisterPayload registerPayload = new RegisterPayload();
            registerPayload.setEmail("newcomer@mango.dqtri.com");
            registerPayload.setPassword("newcomer");
            return registerPayload;
        }

        private CoreUser createCoreUser() {
            CoreUser coreUser = new CoreUser();
            coreUser.setEmail("newcomer@mango.dqtri.com");
            coreUser.setPassword(passwordEncoder.encode("newcomer"));
            coreUser.setRole(Role.SUBMITTER);
            return coreUser;
        }
    }

    @Nested
    class LoginIntegrationTest {
        private static final String LOGIN_ROUTE = "/auth/login";

        @BeforeEach
        public void setup() {

        }

        @Test
        void login_givenUserCredentials_thenSuccess() throws Exception {
            LoginPayload loginPayload = createLoginPayload();
            var authentication = new UsernamePasswordAuthenticationToken(loginPayload.getEmail(), loginPayload.getPassword());
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(tokenProvider.generateToken(any())).thenReturn(new TokenResponse("refresh_token_value", "access_token_value"));
            //then
            TokenResponse tokenResponse = assertOk(loginPayload);
            //test
            assertThat(tokenResponse).isNotNull();
            assertThat(tokenResponse.getRefreshToken()).isEqualTo("refresh_token_value");
            assertThat(tokenResponse.getAccessToken()).isEqualTo("access_token_value");
        }

        @Test
        void login_givenEmptyPayload_thenBadRequest() throws Exception {
            LoginPayload loginPayload = new LoginPayload();
            assertBadRequest(loginPayload);
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalidEmailFormat", "invalidEmailFormat@", "@invalidEmailFormat"})
        void login_givenInvalidEmailFormat_thenBadRequest(String invalidEmail) throws Exception {
            LoginPayload loginPayload = new LoginPayload();
            loginPayload.setEmail(invalidEmail);
            loginPayload.setPassword("******");
            //then
            assertBadRequest(loginPayload);
        }

        @Test
        void login_givenNonPassword_thenBadRequest() throws Exception {
            LoginPayload loginPayload = new LoginPayload();
            loginPayload.setEmail("submitter@mango.dqtri.com");
            //then
            assertBadRequest(loginPayload);
        }

        @ParameterizedTest
        @ValueSource(strings = {"st", "", "       ", "more_than_24_characters_too_long_password"})
        void login_givenInvalidPasswordFormat_thenBadRequest(String password) throws Exception {
            LoginPayload loginPayload = new LoginPayload();
            loginPayload.setEmail("submitter@mango.dqtri.com");
            loginPayload.setPassword(password);
            //then
            assertBadRequest(loginPayload);
        }

        @Test
        void login_mockBadCredentials_thenUnauthorized() throws Exception {
            LoginPayload loginPayload = createLoginPayload();
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
            MvcResult mvcResult = mvc.perform(post(LOGIN_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(loginPayload)))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
            verifyNoInteractions(tokenProvider);
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getMessage()).isEqualTo("Bad credentials");
        }

        private LoginPayload createLoginPayload() {
            LoginPayload loginPayload = new LoginPayload();
            loginPayload.setEmail("submitter@mango.dqtri.com");
            loginPayload.setPassword("submitter");
            return loginPayload;
        }

        private TokenResponse assertOk(LoginPayload registerPayload) throws Exception {
            MvcResult mvcResult = mvc.perform(post(LOGIN_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(registerPayload)))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = mvcResult.getResponse().getContentAsString();
            return new ObjectMapper().readValue(json, TokenResponse.class);
        }

        private void assertBadRequest(LoginPayload registerPayload) throws Exception {
            mvc.perform(post(LOGIN_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(registerPayload)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(authenticationManager);
            verifyNoInteractions(tokenProvider);
        }
    }
}

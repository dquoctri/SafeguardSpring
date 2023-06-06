/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.controller;


import com.dqtri.mango.safeguard.config.SecurityConfig;
import com.dqtri.mango.safeguard.model.SafeguardUser;
import com.dqtri.mango.safeguard.model.dto.payload.LoginPayload;
import com.dqtri.mango.safeguard.model.dto.payload.RegisterPayload;
import com.dqtri.mango.safeguard.model.dto.response.AuthenticationResponse;
import com.dqtri.mango.safeguard.model.dto.response.ErrorResponse;
import com.dqtri.mango.safeguard.model.dto.response.RefreshResponse;
import com.dqtri.mango.safeguard.model.enums.Role;
import com.dqtri.mango.safeguard.repository.BlackListRefreshTokenRepository;
import com.dqtri.mango.safeguard.repository.UserRepository;
import com.dqtri.mango.safeguard.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {AuthController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
public class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BlackListRefreshTokenRepository blackListRefreshTokenRepository;

    @BeforeEach
    public void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Nested
    class RegisterIntegrationTest {
        private static final String REGISTER_ROUTE = "/auth/register";

        @Captor
        ArgumentCaptor<SafeguardUser> userArgumentCaptor;

        @BeforeEach
        public void setup() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
        }

        @Test
        void register_givenUserCredentials_thenSuccess() throws Exception {
            RegisterPayload registerPayload = createRegisterPayload();
            assertOk(registerPayload);
            verify(userRepository).save(userArgumentCaptor.capture());
            SafeguardUser value = userArgumentCaptor.getValue();
            assertThat(value.getRole()).isEqualTo(Role.SUBMITTER);
            verify(userRepository).save(createSafeguardUser());
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

        private SafeguardUser createSafeguardUser() {
            SafeguardUser safeguardUser = new SafeguardUser();
            safeguardUser.setEmail("newcomer@mango.dqtri.com");
            safeguardUser.setPassword(passwordEncoder.encode("newcomer"));
            safeguardUser.setRole(Role.SUBMITTER);
            return safeguardUser;
        }
    }

    @Nested
    class LoginIntegrationTest {
        private static final String LOGIN_ROUTE = "/auth/login";

        @Test
        void login_givenUserCredentials_thenSuccess() throws Exception {
            LoginPayload loginPayload = createLoginPayload();
            var authentication = new UsernamePasswordAuthenticationToken(loginPayload.getEmail(), loginPayload.getPassword());
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(tokenProvider.generateToken(any())).thenReturn("token_value");
            //then
            AuthenticationResponse authenticationResponse = assertOk(loginPayload);
            //test
            assertThat(authenticationResponse).isNotNull();
            assertThat(authenticationResponse.getRefreshToken()).isEqualTo("token_value");
            assertThat(authenticationResponse.getAccessToken()).isEqualTo("token_value");
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

        private AuthenticationResponse assertOk(LoginPayload registerPayload) throws Exception {
            MvcResult mvcResult = mvc.perform(post(LOGIN_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(registerPayload)))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = mvcResult.getResponse().getContentAsString();
            return new ObjectMapper().readValue(json, AuthenticationResponse.class);
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

    @Nested
    class RefreshTokenIntegrationTest {
        private static final String REFRESH_ROUTE = "/auth/refresh";

        @BeforeEach
        public void setup() {
            when(tokenProvider.generateToken(any())).thenReturn("token_value");
        }

        @Test
        @WithMockUser(roles = "REFRESH")
        void refreshToken_mockRefreshRoleCredentials_thenSuccess() throws Exception {
            RefreshResponse refreshResponse = assertOK();
            assertThat(refreshResponse).isNotNull();
            assertThat(refreshResponse.getAccessToken()).isEqualTo("token_value");
        }

        @Test
        @WithMockUser(authorities = "REFRESH")
        void refreshToken_mockRefreshAuthorityCredentials_thenSuccess() throws Exception {
            RefreshResponse refreshResponse = assertOK();
            assertThat(refreshResponse).isNotNull();
            assertThat(refreshResponse.getAccessToken()).isEqualTo("token_value");
        }

        @Test
        void refreshToken_withRefreshRoleCredentials_thenSuccess() throws Exception {
            MvcResult mvcResult = mvc.perform(get(REFRESH_ROUTE)
                            .with(user("email").password("********").roles("REFRESH")))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = mvcResult.getResponse().getContentAsString();
            RefreshResponse refreshResponse = new ObjectMapper().readValue(json, RefreshResponse.class);
            assertThat(refreshResponse).isNotNull();
            assertThat(refreshResponse.getAccessToken()).isEqualTo("token_value");
        }

        @Test
        void refreshToken_withRefreshAuthorityCredentials_thenSuccess() throws Exception {
            MvcResult mvcResult = mvc.perform(get(REFRESH_ROUTE)
                            .with(user("email").password("********")
                                    .authorities(new SimpleGrantedAuthority("REFRESH"))))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = mvcResult.getResponse().getContentAsString();
            RefreshResponse refreshResponse = new ObjectMapper().readValue(json, RefreshResponse.class);
            assertThat(refreshResponse).isNotNull();
            assertThat(refreshResponse.getAccessToken()).isEqualTo("token_value");
        }

        private RefreshResponse assertOK() throws Exception {
            MvcResult mvcResult = mvc.perform(get(REFRESH_ROUTE))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = mvcResult.getResponse().getContentAsString();
            return new ObjectMapper().readValue(json, RefreshResponse.class);
        }

        @Test
        void refreshToken_withAppUser_thenForbidden() throws Exception {
            assertForbidden(mockAdminUser());
            assertForbidden(mockSubmitterUser());
            assertForbidden(mockManagerUser());
            assertForbidden(mockSpecialistUser());
            assertForbidden(mockInactiveUser());
        }

        private void assertForbidden(RequestPostProcessor forbiddenProcessor) throws Exception {
            mvc.perform(get(REFRESH_ROUTE).with(forbiddenProcessor))
                    .andExpect(status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }

        @Test
        @WithMockUser(roles = {"INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE"})
        void refreshToken_mockAppRoles_thenForbidden() throws Exception {
            mvc.perform(get(REFRESH_ROUTE)).andExpect(status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }

        @Test
        @WithMockUser(authorities = {"INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE"})
        void refreshToken_mockAppAuthorities_thenForbidden() throws Exception {
            mvc.perform(get(REFRESH_ROUTE)).andExpect(status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }
    }

    @Nested
    class LogoutIntegrationTest {
        private static final String LOGOUT_ROUTE = "/auth/logout";
        private HttpHeaders headers;

        @BeforeEach
        public void setup() {
            headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "mock_token");
//            userDetailsService.loadUserByUsername()
        }

        @Test
        @WithUserDetails(value = "email@dqtri.com")
//        @WithMockBasicUser(roles = "REFRESH")
        void logoutToken_mockRefreshRoleCredentials_thenSuccess() throws Exception {
            mvc.perform(delete(LOGOUT_ROUTE).headers(headers)).andExpect(status().isNoContent());
            verify(blackListRefreshTokenRepository, times(1)).save(any());
        }

        @Test
        @WithMockUser(authorities = "REFRESH")
        void logoutToken_mockRefreshAuthorityCredentials_thenSuccess() throws Exception {
            mvc.perform(delete(LOGOUT_ROUTE).headers(headers)).andExpect(status().isNoContent());
            verify(blackListRefreshTokenRepository, times(1)).save(any());
        }

        @Test
        void logout_givenNothing_thenUnauthorized() throws Exception {
            mvc.perform(delete(LOGOUT_ROUTE).headers(headers)).andExpect(status().isUnauthorized());
            verify(blackListRefreshTokenRepository, never()).save(any());
        }

        @Test
        @WithMockUser(roles = {"INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE"})
        void logout_mockAppRoles_thenForbidden() throws Exception {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "mock_token");
            mvc.perform(delete(LOGOUT_ROUTE).headers(headers)).andExpect(status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }

        @Test
        @WithMockUser(authorities = {"INVALID", "ADMIN", "SUBMITTER", "MANAGER", "SPECIALIST", "NONE"})
        void logout_mockAppAuthorities_thenForbidden() throws Exception {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "mock_token");
            mvc.perform(delete(LOGOUT_ROUTE).headers(headers)).andExpect(status().isForbidden());
            verify(tokenProvider, never()).generateToken(any());
        }
    }
}

/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.controller;

import com.dqtri.mango.safeguard.common.WithMockAppUser;
import com.dqtri.mango.safeguard.config.SecurityConfig;
import com.dqtri.mango.safeguard.model.SafeguardUser;
import com.dqtri.mango.safeguard.model.dto.response.ErrorResponse;
import com.dqtri.mango.safeguard.model.dto.response.UserResponse;
import com.dqtri.mango.safeguard.model.enums.Role;
import com.dqtri.mango.safeguard.repository.UserRepository;
import com.dqtri.mango.safeguard.util.PageDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ProblemDetail;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {UserController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
public class UserControllerIntegrationTest extends AbstractIntegrationTest {
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;

    @Nested
    class RouteGetAllUsersFeatureIntegrationTest {
        private static final String USER_ROUTE = "/users";
        private ObjectMapper objectMapper;

        @BeforeEach
        public void setup() {
            objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Page.class, new PageDeserializer<>(UserResponse.class));
            objectMapper.registerModule(module);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_hasNoUser_returnEmptyPagination() throws Exception {
            Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            Page<SafeguardUser> usersPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
            when(userRepository.findAll(pageable)).thenReturn(usersPage);
            //then
            MvcResult mvcResult = performRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            Page<UserResponse> result = objectMapper.readValue(json, new TypeReference<>() {
            });
            //test
            assertUserPageResponse(usersPage, result);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_defaultOneUsers_returnPaginationWithSize() throws Exception {
            Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            List<SafeguardUser> users = new ArrayList<>(createUserList(1));
            Page<SafeguardUser> usersPage = new PageImpl<>(users, pageable, 1);
            when(userRepository.findAll(pageable)).thenReturn(usersPage);
            //then
            MvcResult mvcResult = performRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            Page<UserResponse> result = objectMapper.readValue(json, new TypeReference<>() {
            });
            //test
            assertUserPageResponse(usersPage, result);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_AllRoleUsers_returnPaginationWithSize5() throws Exception {
            int numberOfUsers = 5;
            Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            List<SafeguardUser> users = new ArrayList<>(createUserList(numberOfUsers));
            Page<SafeguardUser> usersPage = new PageImpl<>(users, pageable, numberOfUsers);
            when(userRepository.findAll(pageable)).thenReturn(usersPage);
            //then
            MvcResult mvcResult = performRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            Page<UserResponse> result = objectMapper.readValue(json, new TypeReference<>() {
            });
            //test
            assertUserPageResponse(usersPage, result);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_withPageNumberAndSize_returnPaginationWithSize5() throws Exception {
            int pageSize = 25;
            int pageNumber = 1;
            int numberOfUsers = 5;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            List<SafeguardUser> users = new ArrayList<>(createUserList(numberOfUsers));
            Page<SafeguardUser> usersPage = new PageImpl<>(users, pageable, numberOfUsers);
            when(userRepository.findAll(pageable)).thenReturn(usersPage);
            //then
            MvcResult mvcResult = performRequest(pageNumber, pageSize, status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            Page<UserResponse> result = objectMapper.readValue(json, new TypeReference<>() {
            });
            //test
            assertUserPageResponse(usersPage, result);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_pageSizeZero_thenBadRequest() throws Exception {
            int pageSize = 0;
            int pageNumber = 152;
            //then
            MvcResult mvcResult = performRequest(pageNumber, pageSize, status().isBadRequest());
            String json = mvcResult.getResponse().getContentAsString();
            ProblemDetail problemDetail = objectMapper.readValue(json, ProblemDetail.class);
            //test
            assertBadRequest(problemDetail);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_negativePageSize_thenBadRequest() throws Exception {
            int pageSize = -25;
            int pageNumber = 152;
            //then
            MvcResult mvcResult = performRequest(pageNumber, pageSize, status().isBadRequest());
            String json = mvcResult.getResponse().getContentAsString();
            ProblemDetail problemDetail = objectMapper.readValue(json, ProblemDetail.class);
            //test
            assertBadRequest(problemDetail);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_negativePageNumber_thenBadRequest() throws Exception {
            int pageSize = 25;
            int pageNumber = -1;
            //then
            MvcResult mvcResult = performRequest(pageNumber, pageSize, status().isBadRequest());
            String json = mvcResult.getResponse().getContentAsString();
            ProblemDetail problemDetail = objectMapper.readValue(json, ProblemDetail.class);
            //test
            assertBadRequest(problemDetail);
        }

        private void assertBadRequest(ProblemDetail problemDetail){
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getStatus()).isEqualTo(400);
            assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
            assertThat(problemDetail.getDetail()).isEqualTo("Invalid request content.");
        }

        private MvcResult performRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE)).andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(int pageNumber, int pageSize, ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE)
                            .param("pageNumber", "" + pageNumber)
                            .param("pageSize", "" + pageSize))
                    .andExpectAll(matchers).andReturn();
        }

        private void assertUserPageResponse(Page<SafeguardUser> usersPage, Page<UserResponse> result) {
            //test
            assertThat(result).isNotNull();
            assertThat(result.getSize()).isEqualTo(usersPage.getSize());
            assertThat(result.getNumber()).isEqualTo(usersPage.getNumber());
            assertThat(result.getTotalPages()).isEqualTo(usersPage.getTotalPages());
            assertThat(result.getTotalElements()).isEqualTo(usersPage.getTotalElements());
            int size = usersPage.getContent().size();
            assertThat(result.getContent()).hasSize(size);
            for (int i = 0; i < size; i++) {
                assertUserResponse(usersPage.getContent().get(i), result.getContent().get(i));
            }
        }

        private void assertUserResponse(SafeguardUser user, UserResponse response) {
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(user.getPk());
            assertThat(response.getEmail()).isEqualTo(user.getEmail());
            assertThat(response.getRole()).isEqualTo(user.getRole());
        }

        private List<SafeguardUser> createUserList(int numberOfUsers) {
            List<SafeguardUser> users = new ArrayList<>();
            Role[] roles = Role.values();
            for (int i = 0; i < numberOfUsers; i++) {
                SafeguardUser safeguardUser = new SafeguardUser();
                safeguardUser.setPk((long) i);
                safeguardUser.setEmail(String.format("user%s@dqtri.com", i));
                safeguardUser.setRole(roles[i % 5]);
            }
            return users;
        }
    }

    @Nested
    class RouteGetUserFeatureIntegrationTest {
        private static final String USER_PROFILE_ROUTE = "/users/me";

        @Test
        @WithMockAppUser(email = "mango@dqtri.com", role = Role.SUBMITTER)
        void getProfiles_mockUser_thenUserResponse() throws Exception {
            MvcResult mvcResult = performRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            UserResponse userResponse = new ObjectMapper().readValue(json, UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertThat(userResponse.getEmail()).isEqualTo("mango@dqtri.com");
            assertThat(userResponse.getRole()).isEqualTo(Role.SUBMITTER);
        }

        private MvcResult performRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_PROFILE_ROUTE))
                    .andExpectAll(matchers).andReturn();
        }
    }


}

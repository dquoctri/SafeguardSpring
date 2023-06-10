/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.controller;

import com.dqtri.mango.safeguard.config.SecurityConfig;
import com.dqtri.mango.safeguard.model.SafeguardUser;
import com.dqtri.mango.safeguard.model.dto.PageCriteria;
import com.dqtri.mango.safeguard.model.enums.Role;
import com.dqtri.mango.safeguard.repository.UserRepository;
import com.dqtri.mango.safeguard.util.PageDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.ArrayList;
import java.util.List;

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
    class RouteGetAllUsersSecurityIntegrationTest {
        private static final String USER_ROUTE = "/users";

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_defaultPageSize_returnPagination() throws Exception {
            MvcResult mvcResult = performRequest(status().isOk());
        }

        private MvcResult performRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE)).andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(RequestPostProcessor forbiddenProcessor, ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE).with(forbiddenProcessor)).andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RouteGetAllUsersFeatureIntegrationTest {
        private static final String USER_ROUTE = "/users";

        @Captor
        private ArgumentCaptor<Pageable> pageableCaptor;

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_defaultPageSize_returnPagination() throws Exception {
            PageCriteria pageCriteria = new PageCriteria(0, 25);
            Pageable pageable = pageCriteria.toPageable("email");
            List<SafeguardUser> users = new ArrayList<>();
            SafeguardUser safeguardUser = new SafeguardUser();
            safeguardUser.setPk(3L);
            safeguardUser.setEmail("abc@abc.com");
            safeguardUser.setRole(Role.NONE);
            users.add(safeguardUser);
            Page<SafeguardUser> usersPage = new PageImpl<>(users, pageable, 1);
//            Page<SafeguardUser> usersPage = new PageImpl<>(users, pageableCaptor.capture(), 1);
            when(userRepository.findAll(pageableCaptor.capture())).thenReturn(usersPage);

            MvcResult mvcResult = performRequest(status().isOk());
            String json = mvcResult.getResponse().getContentAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Page.class, new PageDeserializer<>(SafeguardUser.class));
            objectMapper.registerModule(module);

            Page<SafeguardUser> result = objectMapper.readValue(json, new TypeReference<>() {});

        }

        private MvcResult performRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE)).andExpectAll(matchers).andReturn();
        }

        private PageCriteria createPageCriteria() {
            return new PageCriteria(0, 25);
        }
    }
}

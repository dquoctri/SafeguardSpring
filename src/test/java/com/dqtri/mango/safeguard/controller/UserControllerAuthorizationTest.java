package com.dqtri.mango.safeguard.controller;

import com.dqtri.mango.safeguard.common.WithMockAppUser;
import com.dqtri.mango.safeguard.config.SecurityConfig;
import com.dqtri.mango.safeguard.model.SafeguardUser;
import com.dqtri.mango.safeguard.model.dto.payload.UserCreatingPayload;
import com.dqtri.mango.safeguard.model.dto.payload.UserUpdatingPayload;
import com.dqtri.mango.safeguard.model.dto.response.ErrorResponse;
import com.dqtri.mango.safeguard.model.enums.Role;
import com.dqtri.mango.safeguard.repository.UserRepository;
import com.dqtri.mango.safeguard.security.permissions.UpdatableResourcePermission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = {UserController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, UpdatableResourcePermission.class}))
public class UserControllerAuthorizationTest extends AbstractIntegrationTest {
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;

    @Nested
    class RouteGetAllUsersAuthorizationIntegrationTest {
        private static final String USER_ROUTE = "/users";

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_defaultAdmin_returnPagination() throws Exception {
            Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.DEFAULT_DIRECTION, "pk"));
            Page<SafeguardUser> usersPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
            when(userRepository.findAll(pageable)).thenReturn(usersPage);
            //then
            performRequest(status().isOk());
        }

        @Test
        void getAllUsers_nonMockUser_returnUnauthorized() throws Exception {
            performRequest(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void getAllUsers_giveNonAdminRoles_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(status().isForbidden());
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            //test
            assertForbiddenResponse(errorResponse);
        }

        @Test
        @WithMockUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void getAllUsers_giveNonAdminAuthority_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(status().isForbidden());
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            //test
            assertForbiddenResponse(errorResponse);
        }

        @Test
        void getAllUsers_mockAuthorityOfOthers_thenForbidden() throws Exception {
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE",
                            "REFRESH", "INVALID"));
            MvcResult mvcResult = performRequest(user, status().isForbidden());
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            //test
            assertForbiddenResponse(errorResponse);
        }

        private void assertForbiddenResponse(ErrorResponse errorResponse) {
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private MvcResult performRequest(ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE)).andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(RequestPostProcessor processor,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE).with(processor)).andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RouteGetUserAuthorizationIntegrationTest {
        private static final String USER_ROUTE = "/users/{userId}";

        @Test
        @WithMockUser(roles = "ADMIN")
        void getUserById_defaultAdmin_thenOk() throws Exception {
            when(userRepository.findById(1L)).thenReturn(Optional.of(new SafeguardUser()));
            //then
            performRequest(1L, status().isOk());
        }

        @Test
        void getUserById_withAdmin_thenOk() throws Exception {
            when(userRepository.findById(4L)).thenReturn(Optional.of(new SafeguardUser()));
            RequestPostProcessor user = user("admin@dqtri.com").password("******").roles("ADMIN");
            performRequest(4L, user, status().isOk());
        }

        @Test
        void getUserById_nonMockUser_returnUnauthorized() throws Exception {
            performRequest(5L, status().isUnauthorized());
            verify(userRepository, never()).findById(5L);
        }

        @Test
        @WithMockUser(roles = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void getUserById_giveNonAdminRoles_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(4L, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        @WithMockUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void getUserById_giveNonAdminAuthority_thenForbidden() throws Exception {
            MvcResult mvcResult = performRequest(3L, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        void getUserById_mockAuthorityOfOthers_thenForbidden() throws Exception {
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"));
            MvcResult mvcResult = performRequest(2L, user, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        private void assertForbiddenResponse(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private MvcResult performRequest(long userId, ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE, userId))
                    .andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(long userId, RequestPostProcessor processor,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(get(USER_ROUTE, userId)
                    .with(processor)).andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RouteGetProfilesAuthorizationIntegrationTest {
        private static final String USER_PROFILE_ROUTE = "/users/me";

        @Test
        @WithMockAppUser(roles = {"ADMIN", "MANAGER", "SUBMITTER", "SPECIALIST", "NONE"})
        void getProfiles_givenAllRoles_returnOk() throws Exception {
            performRequest(status().isOk());
        }

        @Test
        @WithMockAppUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE"})
        void getProfiles_giveAllAuthorities_returnOk() throws Exception {
            performRequest(status().isOk());
        }

        @Test
        void getProfiles_nonMockUser_returnUnauthorized() throws Exception {
            performRequest(status().isUnauthorized());
        }

        @Test
        void getProfiles_mockProcessor_thenOk() throws Exception {
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE"));
            performRequest(user, status().isOk());
        }

        private void performRequest(ResultMatcher... matchers) throws Exception {
            mvc.perform(get(USER_PROFILE_ROUTE))
                    .andExpectAll(matchers).andReturn();
        }

        private void performRequest(RequestPostProcessor processor,
                                    ResultMatcher... matchers) throws Exception {
            mvc.perform(get(USER_PROFILE_ROUTE)
                    .with(processor)).andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RouteCreateUserAuthorizationIntegrationTest {
        private static final String USER_ROUTE = "/users";

        @Captor
        ArgumentCaptor<SafeguardUser> userArgumentCaptor;

        @BeforeEach
        public void setup() {
            passwordEncoder = new BCryptPasswordEncoder();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getCreateUser_defaultAdmin_returnCreated() throws Exception {
            when(userRepository.save(any())).thenReturn(createSafeguardUser());
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            //then
            performRequest(userCreatingPayload, status().isCreated());
            //test
            verify(userRepository).save(userArgumentCaptor.capture());
            SafeguardUser value = userArgumentCaptor.getValue();
            assertThat(value.getRole()).isEqualTo(Role.SUBMITTER);
        }

        @Test
        void getCreateUser_nonMockUser_returnUnauthorized() throws Exception {
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            performRequest(userCreatingPayload, status().isUnauthorized());
            verify(userRepository, never()).save(any());
        }

        @Test
        @WithMockUser(roles = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void getCreateUser_giveNonAdminRoles_thenForbidden() throws Exception {
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            MvcResult mvcResult = performRequest(userCreatingPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        @WithMockUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void getCreateUser_giveNonAdminAuthority_thenForbidden() throws Exception {
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            MvcResult mvcResult = performRequest(userCreatingPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        void getCreateUser_mockAuthorityOfOthers_thenForbidden() throws Exception {
            UserCreatingPayload userCreatingPayload = createUserCreatingPayload();
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"));
            MvcResult mvcResult = performRequest(userCreatingPayload, user, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        private void assertForbiddenResponse(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private UserCreatingPayload createUserCreatingPayload() {
            UserCreatingPayload userCreatingPayload = new UserCreatingPayload();
            userCreatingPayload.setEmail("newcomer@mango.dqtri.com");
            userCreatingPayload.setPassword("newcomer");
            userCreatingPayload.setRole(Role.SUBMITTER);
            return userCreatingPayload;
        }

        private SafeguardUser createSafeguardUser() {
            SafeguardUser safeguardUser = new SafeguardUser();
            safeguardUser.setEmail("newcomer@mango.dqtri.com");
            safeguardUser.setPassword(passwordEncoder.encode("newcomer"));
            safeguardUser.setRole(Role.SUBMITTER);
            return safeguardUser;
        }

        private MvcResult performRequest(UserCreatingPayload userCreatingPayload,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(post(USER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userCreatingPayload)))
                    .andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(UserCreatingPayload userCreatingPayload,
                                         RequestPostProcessor processor,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(post(USER_ROUTE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userCreatingPayload))
                            .with(processor))
                    .andExpectAll(matchers).andReturn();
        }
    }

    @Nested
    class RoutUpdateUserAuthorizationIntegrationTest {

        private static final String USER_ROUTE = "/users/{userId}";

        @BeforeEach
        public void setup() {
            passwordEncoder = new BCryptPasswordEncoder();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUser_defaultAdmin_thenUpdated() throws Exception {
            when(userRepository.findById(1L)).thenReturn(Optional.of(new SafeguardUser()));
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            //then
            performRequest(1L, userUpdatingPayload, status().isOk());
        }

        @Test
        void updateUser_withAdmin_thenUpdated() throws Exception {
            when(userRepository.findById(2L)).thenReturn(Optional.of(new SafeguardUser()));
            RequestPostProcessor user = user("admin@dqtri.com").password("******").roles("ADMIN");
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            //then
            performRequest(2L, userUpdatingPayload, user, status().isOk());
        }

        @Test
        void getProfiles_nonMockUser_returnUnauthorized() throws Exception {
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            performRequest(4L, userUpdatingPayload, status().isUnauthorized());
            verify(userRepository, never()).findById(4L);
        }

        @Test
        @WithMockUser(roles = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void getUserById_giveNonAdminRoles_thenForbidden() throws Exception {
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            MvcResult mvcResult = performRequest(4L, userUpdatingPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        @WithMockUser(authorities = {"MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"})
        void getUserById_giveNonAdminAuthority_thenForbidden() throws Exception {
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            MvcResult mvcResult = performRequest(3L, userUpdatingPayload, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        @Test
        void getUserById_mockAuthorityOfOthers_thenForbidden() throws Exception {
            RequestPostProcessor user = user("appuser@dqtri.com").password("******")
                    .roles("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID")
                    .authorities(buildAuthorities("MANAGER", "SUBMITTER", "SPECIALIST", "NONE", "REFRESH", "INVALID"));
            UserUpdatingPayload userUpdatingPayload = createUserUpdatingPayload();
            MvcResult mvcResult = performRequest(4L, userUpdatingPayload, user, status().isForbidden());
            //test
            assertForbiddenResponse(mvcResult);
        }

        private void assertForbiddenResponse(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = new ObjectMapper().readValue(json, ErrorResponse.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(errorResponse.getMessage()).isEqualTo("Access Denied");
        }

        private UserUpdatingPayload createUserUpdatingPayload() {
            UserUpdatingPayload userUpdatingPayload = new UserUpdatingPayload();
            userUpdatingPayload.setRole(Role.MANAGER);
            return userUpdatingPayload;
        }

        private MvcResult performRequest(long userId,
                                         UserUpdatingPayload userUpdatingPayload,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(put(USER_ROUTE, userId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userUpdatingPayload)))
                    .andExpectAll(matchers).andReturn();
        }

        private MvcResult performRequest(long userId,
                                         UserUpdatingPayload userUpdatingPayload,
                                         RequestPostProcessor processor,
                                         ResultMatcher... matchers) throws Exception {
            return mvc.perform(put(USER_ROUTE, userId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(createPayloadJson(userUpdatingPayload))
                            .with(processor))
                    .andExpectAll(matchers).andReturn();
        }
    }
}

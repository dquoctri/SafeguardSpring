/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.controller;

import com.dqtri.mango.safeguard.exception.ConflictException;
import com.dqtri.mango.safeguard.model.CoreUser;
import com.dqtri.mango.safeguard.model.dto.payload.LoginPayload;
import com.dqtri.mango.safeguard.model.dto.payload.RegisterPayload;
import com.dqtri.mango.safeguard.model.dto.response.TokenResponse;
import com.dqtri.mango.safeguard.model.enums.Role;
import com.dqtri.mango.safeguard.repository.UserRepository;
import com.dqtri.mango.safeguard.security.TokenProvider;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "Responsible for handling user authentication-related operations")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Operation(summary = "Register by providing necessary registration details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registered user details",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CoreUser.class)) }),
            @ApiResponse(responseCode = "400", description = "The provided email or password format is invalid"),
    })
    @PostMapping(value = "/register")
    @Transactional
    public ResponseEntity<CoreUser> register(@RequestBody @Valid RegisterPayload register) {
        checkConflictUserEmail(register.getEmail());
        CoreUser coreUser = createCoreUser(register);
        CoreUser saved = userRepository.save(coreUser);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Login by providing email & password credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generate a refresh token",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "The provided email or password format is invalid"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password credentials"),
    })
    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginPayload login) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenResponse tokenResponse = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(tokenResponse);
    }

    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid LoginPayload login) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenResponse tokenResponse = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(tokenResponse);
    }

    private void checkConflictUserEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("%s is already in use", email));
        }
    }

    private CoreUser createCoreUser(@NotNull RegisterPayload register) {
        CoreUser user = new CoreUser();
        user.setEmail(register.getEmail());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setRole(Role.SUBMITTER);
        return user;
    }

    @Hidden
    @PostMapping("forgot-password")
    public ResponseEntity<String> forgotPassword() {
        return ResponseEntity.ok("forgot_password");
    }

    @Hidden
    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword() {
        return ResponseEntity.ok("reset_password");
    }

    @Hidden
    @PostMapping("change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword() {

        return ResponseEntity.ok("change_password");
    }
}

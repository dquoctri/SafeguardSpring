package com.dqtri.mango.safeguard.controller;

import com.dqtri.mango.safeguard.annotation.AuthenticationApiResponses;
import com.dqtri.mango.safeguard.annotation.NotFound404ApiResponses;
import com.dqtri.mango.safeguard.annotation.Validation400ApiResponses;
import com.dqtri.mango.safeguard.exception.ConflictException;
import com.dqtri.mango.safeguard.model.SafeguardUser;
import com.dqtri.mango.safeguard.model.dto.PageCriteria;
import com.dqtri.mango.safeguard.model.dto.payload.ResetPasswordPayload;
import com.dqtri.mango.safeguard.model.dto.payload.UserCreatingPayload;
import com.dqtri.mango.safeguard.model.dto.payload.UserUpdatingPayload;
import com.dqtri.mango.safeguard.model.dto.response.ErrorResponse;
import com.dqtri.mango.safeguard.model.dto.response.UserResponse;
import com.dqtri.mango.safeguard.model.enums.Role;
import com.dqtri.mango.safeguard.repository.UserRepository;
import com.dqtri.mango.safeguard.security.AppUserDetails;
import com.dqtri.mango.safeguard.security.UserPrincipal;
import com.dqtri.mango.safeguard.util.Helper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "access_token")
@Tag(name = "User API", description = "Endpoints for managing user operations")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Operation(summary = "Get users", description = "Retrieve a paginated list of users")
    @AuthenticationApiResponses
    @Validation400ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval a paginated list of users",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class, subTypes = UserResponse.class))}),
    })
    @Transactional(readOnly = true)
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SPECIALIST', 'SUBMITTER')")
    public ResponseEntity<Page<UserResponse>> getUsers(@Valid PageCriteria pageCriteria) {
        PageRequest pageable = pageCriteria.toPageable("pk");
        Page<SafeguardUser> page = userRepository.findAll(pageable);
        List<UserResponse> userResponses = UserResponse.buildFromUsers(page.getContent());
        Page<UserResponse> pagination = Helper.createPagination(userResponses, page);
        return ResponseEntity.ok(pagination);
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user information based on the provided ID")
    @AuthenticationApiResponses
    @NotFound404ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of the user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))})
    })
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SPECIALIST', 'SUBMITTER')")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") Long userId) {
        SafeguardUser safeguardUser = getUserOrElseThrow(userId);
        return ResponseEntity.ok(new UserResponse(safeguardUser));
    }

    @Operation(summary = "Get my profiles", description = "Retrieve profiles of the currently authenticated user")
    @AuthenticationApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of profiles",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))}),
    })
    @GetMapping("/users/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMyProfiles(@UserPrincipal AppUserDetails currentUser) {
        if (currentUser != null) {
            SafeguardUser safeguardUser = currentUser.getSafeguardUser();
            return ResponseEntity.ok(new UserResponse(safeguardUser));
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create user", description = "Create a new user with the provided details")
    @AuthenticationApiResponses
    @Validation400ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful creation of user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "409", description = "Email is already in use",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping(value = "/users", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreatingPayload payload) {
        validateUniqueEmail(payload.getEmail());
        SafeguardUser saved = userRepository.save(createSafeguardUser(payload));
        return new ResponseEntity<>(new UserResponse(saved), HttpStatus.CREATED);
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format("%s is already in use", email));
        }
    }

    private SafeguardUser createSafeguardUser(@NotNull UserCreatingPayload payload) {
        SafeguardUser user = new SafeguardUser();
        user.setEmail(payload.getEmail());
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        user.setRole(payload.getRole());
        return user;
    }

    @Operation(summary = "Update user", description = "Update an existing user with the provided details.")
    @AuthenticationApiResponses
    @Validation400ApiResponses
    @NotFound404ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))}),
    })
    @PutMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, @updatableResource)")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("userId") Long userId,
                                                   @Valid @RequestBody UserUpdatingPayload payload) {
        SafeguardUser user = getUserOrElseThrow(userId);
        user.setRole(payload.getRole());
        return ResponseEntity.ok(new UserResponse(user));
    }

    @Operation(summary = "Update user password", description = "Update the password of an existing user.")
    @AuthenticationApiResponses
    @Validation400ApiResponses
    @NotFound404ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of user password"),
    })
    @PutMapping("/users/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, @updatableResource)")
    public ResponseEntity<Void> updateUserPassword(@PathVariable("userId") Long userId,
                                                   @Valid @RequestBody ResetPasswordPayload payload) {
        SafeguardUser user = getUserOrElseThrow(userId);
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete user", description = "Delete an existing user.")
    @AuthenticationApiResponses
    @NotFound404ApiResponses
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful deletion of user"),
    })
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, @updatableResource)")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        SafeguardUser user = getUserOrElseThrow(userId);
        user.setRole(Role.NONE);
        return ResponseEntity.noContent().build();
    }

    private SafeguardUser getUserOrElseThrow(@NotNull Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User is not found with id: %s", id)));
    }
}

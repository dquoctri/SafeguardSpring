package com.dqtri.mango.safeguard.controller;

import com.dqtri.mango.safeguard.model.LoginAttempt;
import com.dqtri.mango.safeguard.model.SafeguardUser;
import com.dqtri.mango.safeguard.model.Submission;
import com.dqtri.mango.safeguard.repository.LoginAttemptRepository;
import com.dqtri.mango.safeguard.repository.SubmissionRepository;
import com.dqtri.mango.safeguard.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cleanup")
@RequiredArgsConstructor
@Profile({"development", "formal_test"})
@Tag(name = "Cleanup API", description = "Endpoints for cleanup data for testing develop only")
public class CleanupController {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final LoginAttemptRepository loginAttemptRepository;

    @DeleteMapping
    public ResponseEntity<String> cleanUpData() {
        // Logic to clean up data
        return ResponseEntity.ok("Data cleaned up successfully");
    }

    @DeleteMapping("/users/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> cleanupUsers(@PathVariable String email) {
        List<Submission> submissions = submissionRepository.findAllBySubmitterEmail(email);
        submissionRepository.deleteAll(submissions);

        Optional<SafeguardUser> byEmail = userRepository.findByEmail(email);
        byEmail.ifPresent(userRepository::delete);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/login-attempt/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> cleanupLoginAttempt(@PathVariable String email) {
        Optional<LoginAttempt> byEmail = loginAttemptRepository.findByEmail(email);
        byEmail.ifPresent(loginAttemptRepository::delete);
        return ResponseEntity.noContent().build();
    }
}

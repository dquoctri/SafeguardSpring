/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.controller;

import com.dqtri.mango.safeguard.model.Submission;
import com.dqtri.mango.safeguard.model.dto.PageCriteria;
import com.dqtri.mango.safeguard.model.dto.payload.SubmissionPayload;
import com.dqtri.mango.safeguard.repository.SubmissionRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "access_token")
@Tag(name = "Submission API", description = "Submissions ...")
public class SubmissionController {

    private final SubmissionRepository submissionRepository;

    @Operation(summary = "Get All Submissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission list with paging",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)) })
    })
    @Transactional(readOnly = true)
    @GetMapping("/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SPECIALIST', 'SUBMITTER')")
    public ResponseEntity<Page<Submission>> getSubmissions(@Valid PageCriteria pageCriteria) {
        Pageable pageable = pageCriteria.toPageable("pk");
        Page<Submission> all = submissionRepository.findAll(pageable);
        return ResponseEntity.ok(all);
    }

    @Transactional(readOnly = true)
    @GetMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SPECIALIST', 'SUBMITTER')")
    public ResponseEntity<?> getSubmission(@PathVariable("id") Long id) {
        Submission submission = getSubmissionOrElseThrow(id);
        return ResponseEntity.ok(submission);
    }

    @Transactional
    @PostMapping("/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER')")
    public ResponseEntity<?> createSubmission(@RequestBody @Valid SubmissionPayload submissionPayload) {
        Submission submission = submissionPayload.toSubmission();
        Submission save = submissionRepository.save(submission);
        return ResponseEntity.ok(save);
    }

    @Transactional
    @PutMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER')")
    public ResponseEntity<?> updateSubmission(@PathVariable("id") Long id,
                                              @RequestBody @Valid SubmissionPayload submissionPayload) {
        Submission submission = getSubmissionOrElseThrow(id);
        submission.setSubject(submissionPayload.getSubject());
        Submission saved = submissionRepository.save(submission);
        return ResponseEntity.ok(saved);
    }

    @Transactional
    @DeleteMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBMITTER')")
    public ResponseEntity<?> updateSubmission(@PathVariable("id") Long id) {
        Submission submission = getSubmissionOrElseThrow(id);
        submissionRepository.delete(submission);
        return ResponseEntity.ok().build();
    }

    private Submission getSubmissionOrElseThrow(Long id) {
        return submissionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Submission is not found with id: %s", id))
        );
    }
}


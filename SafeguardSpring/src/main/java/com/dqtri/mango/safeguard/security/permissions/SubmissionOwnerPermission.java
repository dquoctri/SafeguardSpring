package com.dqtri.mango.safeguard.security.permissions;

import com.dqtri.mango.safeguard.model.SafeguardUser;
import com.dqtri.mango.safeguard.model.Submission;
import com.dqtri.mango.safeguard.model.enums.Role;
import com.dqtri.mango.safeguard.repository.SubmissionRepository;
import com.dqtri.mango.safeguard.security.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
@Component("submissionOwner")
public class SubmissionOwnerPermission extends Permission {

    private final SubmissionRepository submissionRepository;

    @Override
    public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
        AppUserDetails currentUser = (AppUserDetails) authentication.getPrincipal();
        SafeguardUser safeguardUser = currentUser.getSafeguardUser();
        if(Role.ADMIN.equals(safeguardUser.getRole())) return true;

        long submissionId = (long) targetDomainObject;
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Submission is not found with id: %s", submissionId))
        );
        return safeguardUser.equals(submission.getSubmitter());
    }

    @Override
    public boolean isAllowed(Authentication authentication, Serializable targetId, String targetType) {
        return false;
    }
}
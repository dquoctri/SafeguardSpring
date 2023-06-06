/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.model.dto.payload;

import com.dqtri.mango.safeguard.model.Submission;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
public class SubmissionPayload {
    @NotBlank
    @Length(max = 152)
    private String content;

    public Submission toSubmission() {
        Submission submission = new Submission();
        submission.setContent(this.content);
        return submission;
    }
}

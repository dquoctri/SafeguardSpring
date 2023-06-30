package com.dqtri.mango.safeguard.model.dto;

import com.dqtri.mango.safeguard.model.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionPageCriteria extends PageCriteria {
    @Schema(example = "AWAITING_APPROVAL")
    private Status status;
    @Schema(example = "mango@dqtri.com")
    private String submitterEmail;
}

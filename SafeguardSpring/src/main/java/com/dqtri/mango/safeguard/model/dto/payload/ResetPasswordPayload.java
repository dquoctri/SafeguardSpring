package com.dqtri.mango.safeguard.model.dto.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordPayload {
    @NotBlank
    private String password;
}
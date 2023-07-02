package com.dqtri.mango.safeguard.model.dto.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ResetPasswordPayload {
    @NotBlank
    private String password;
}

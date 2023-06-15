package com.dqtri.mango.safeguard.model.dto.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AssignPayload {
    @NotNull
    @Email
    private String email;
}
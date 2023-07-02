package com.dqtri.mango.safeguard.model.dto.payload;

import com.dqtri.mango.safeguard.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserCreatingPayload {
    @NotNull
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}

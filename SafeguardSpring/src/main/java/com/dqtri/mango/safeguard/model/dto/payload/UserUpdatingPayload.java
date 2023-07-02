package com.dqtri.mango.safeguard.model.dto.payload;

import com.dqtri.mango.safeguard.model.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserUpdatingPayload {
    @NotNull
    private Role role;
}

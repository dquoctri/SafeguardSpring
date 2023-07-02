package com.dqtri.mango.safeguard.model.dto;

import com.dqtri.mango.safeguard.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserPageCriteria extends PageCriteria {
    @Schema(example = "SUBMITTER")
    private Role role;
}

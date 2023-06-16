package com.dqtri.mango.safeguard.model.dto.response;

import com.dqtri.mango.safeguard.model.SafeguardUser;
import com.dqtri.mango.safeguard.model.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private Role role;

    public UserResponse(SafeguardUser appUser) {
        this.id = appUser.getPk();
        this.email = appUser.getEmail();
        this.role = appUser.getRole();
    }

    public static List<UserResponse> buildFromUsers(List<SafeguardUser> appUsers) {
        return appUsers.stream().map(UserResponse::new).toList();
    }
}

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
    private String email;
    private Role role;

    public UserResponse(SafeguardUser appUser){
        this.email = appUser.getEmail();
        this.role = appUser.getRole();
    }

    public List<UserResponse> parseToUserResponse(List<SafeguardUser> appUsers){
        return appUsers.stream().map(UserResponse::new).toList();
    }
}

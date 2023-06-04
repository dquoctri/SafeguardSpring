package com.dqtri.mango.safeguard.security;

import com.dqtri.mango.safeguard.model.CoreUser;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class BasicUserDetails extends User implements UserDetails {

    @NotNull
    private CoreUser coreUser;

    public BasicUserDetails(@NotNull CoreUser user) {
        super(user.getEmail(), user.getPassword(), createRefreshAuthorities());
        this.coreUser = user;
    }

    public static BasicUserDetails create(@NotNull CoreUser user) {
        return new BasicUserDetails(user);
    }

    private static List<SimpleGrantedAuthority> createRefreshAuthorities() {
        return List.of(new SimpleGrantedAuthority("REFRESH"), new SimpleGrantedAuthority("ROLE_REFRESH"));
    }
}
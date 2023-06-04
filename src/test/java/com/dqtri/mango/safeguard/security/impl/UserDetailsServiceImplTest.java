/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.security.impl;

import com.dqtri.mango.safeguard.model.enums.Role;
import com.dqtri.mango.safeguard.repository.UserRepository;
import com.dqtri.mango.safeguard.security.AppUserDetails;
import com.dqtri.mango.safeguard.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {UserDetailsServiceImpl.class})
public class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setup(){
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    public void loadUserByUsername_givenExistUsername_returnsConfigUser() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("submitter@mango.dqtri.com");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(AppUserDetails.class);
        AppUserDetails appUserDetails = (AppUserDetails)userDetails;
        assertThat(appUserDetails.getCoreUser()).isNotNull();
        assertThat(appUserDetails.getCoreUser().getRole()).isEqualTo(Role.SUBMITTER);
    }

    @Test
    public void loadUserByUsername_givenNOTExistUsername_thenThrowNotFound() {
        Executable executable = () -> userDetailsService.loadUserByUsername("no@dqtri.com");
        //test
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                executable,
                String.format("Username not found: %s", "no@dqtri.com")
        );
        assertThat(exception.getMessage()).isEqualTo("Username not found: no@dqtri.com");
    }

    @Test
    public void loadUserByUsername_givenNullUsername_thenThrowNotFound() {
        Executable executable = () -> userDetailsService.loadUserByUsername(null);
        //test
        assertThrows(
                UsernameNotFoundException.class,
                executable,
                String.format("Username not found: %s", "null")
        );
    }
}

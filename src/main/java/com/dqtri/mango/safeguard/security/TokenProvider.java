/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.security;

import com.dqtri.mango.safeguard.model.dto.response.TokenResponse;
import org.springframework.security.core.Authentication;

public interface TokenProvider {
    TokenResponse generateToken(Authentication authentication) throws Exception;
}

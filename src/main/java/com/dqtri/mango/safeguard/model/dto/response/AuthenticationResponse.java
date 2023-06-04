/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String refreshToken;
    private String accessToken;
}
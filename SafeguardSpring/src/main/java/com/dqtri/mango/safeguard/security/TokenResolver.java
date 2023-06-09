package com.dqtri.mango.safeguard.security;

import org.springframework.security.core.Authentication;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface TokenResolver {
    Authentication verifyToken(String token);
}

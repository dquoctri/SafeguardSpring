package com.dqtri.mango.safeguard.security.access;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.dqtri.mango.safeguard.util.Constant.getAuthorizationToken;
import static com.dqtri.mango.safeguard.util.Constant.isPreflightRequest;
import static com.dqtri.mango.safeguard.util.Constant.isRefreshRequest;
import static com.dqtri.mango.safeguard.util.Constant.validateToken;

@Slf4j
@RequiredArgsConstructor
public class AccessAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!isPreflightRequest(request) && !isRefreshRequest(request)) {
            try {
                String accessToken = getAuthorizationToken(request);
                if (StringUtils.hasText(accessToken) && validateToken(accessToken)) {
                    AccessAuthenticationToken accessAuthenticationToken = new AccessAuthenticationToken(accessToken);
                    Authentication authentication = authenticationManager.authenticate(accessAuthenticationToken);
                    log.debug("Logging in with [{}]", authentication.getPrincipal());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ProviderNotFoundException e) {
                log.error(e.getMessage());
            } catch (Exception e) {
                log.error("Could not set authentication in security context", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}

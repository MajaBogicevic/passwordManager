package org.service.passwordman.infrastructure.security;

import java.io.IOException;
import java.util.Collections;

import org.service.passwordman.application.port.TokenBlacklistStore;
import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.port.UserAuthInvalidationStore;
import org.service.passwordman.application.security.TokenPayload;
import org.service.passwordman.domain.exception.TokenValidationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final TokenBlacklistStore tokenBlacklistStore;
    private final UserAuthInvalidationStore userAuthInvalidationStore;

    public JwtAuthenticationFilter(
            TokenService tokenService,
            TokenBlacklistStore tokenBlacklistStore,
            UserAuthInvalidationStore userAuthInvalidationStore
    ) {
        this.tokenService = tokenService;
        this.tokenBlacklistStore = tokenBlacklistStore;
        this.userAuthInvalidationStore = userAuthInvalidationStore;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            writeUnauthorized(response, "Invalid bearer token.");
            return;
        }

        try {
            TokenPayload tokenPayload = tokenService.parseAccessToken(token);

            if (tokenBlacklistStore.isBlacklisted(tokenPayload.getJwtTokenId())) {
                throw new TokenValidationException("Token is revoked.");
            }

            long issuedAtMillis = tokenService.extractIssuedAtMillis(token);
            long validAfterMillis = userAuthInvalidationStore.getTokensValidAfterForUser(tokenPayload.getUserId());

            if (issuedAtMillis < validAfterMillis) {
                throw new TokenValidationException("Token was invalidated by a security event.");
            }

            long expiresAtMillis = tokenService.extractExpirationMillis(token);

            AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                    tokenPayload.getUserId(),
                    tokenPayload.getUsername(),
                    tokenPayload.getSessionId(),
                    tokenPayload.getJwtTokenId(),
                    expiresAtMillis
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            authenticatedUser,
                            null,
                            Collections.emptyList()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (TokenValidationException ex) {
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, "Invalid or expired access token.");
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
                {
                  "code": "UNAUTHORIZED",
                  "message": "%s"
                }
                """.formatted(message));
    }
}
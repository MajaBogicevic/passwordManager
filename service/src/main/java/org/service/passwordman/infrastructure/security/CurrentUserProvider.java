package org.service.passwordman.infrastructure.security;

import org.service.passwordman.domain.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public AuthenticatedUser requireAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        return authenticatedUser;
    }

    public int requireUserId() {
        return requireAuthenticatedUser().getUserId();
    }

    public String requireUsername() {
        return requireAuthenticatedUser().getUsername();
    }

    public String requireJwtTokenId() {
        return requireAuthenticatedUser().getJwtTokenId();
    }
}
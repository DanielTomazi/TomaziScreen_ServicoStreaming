package com.tomazi.streaming.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurityService {

    public boolean isOwner(Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        return username != null && userId != null;
    }

    public boolean canAccessContent(Long contentId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return true;
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication != null &&
               authentication.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isModerator(Authentication authentication) {
        return authentication != null &&
               authentication.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals("ROLE_MODERATOR") ||
                                   auth.getAuthority().equals("ROLE_ADMIN"));
    }
}

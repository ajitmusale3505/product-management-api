package com.zestindia.productmanagement.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    public static String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null
                        ||
                !authentication.isAuthenticated()
        ) {
            return "SYSTEM";
        }

        String username = authentication.getName();

        if (
                username == null
                        ||
                "anonymousUser".equals(username)
        ) {
            return "SYSTEM";
        }

        return username;
    }
}
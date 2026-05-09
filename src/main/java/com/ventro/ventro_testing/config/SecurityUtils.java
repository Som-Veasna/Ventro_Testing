package com.ventro.ventro_testing.config;

import com.ventro.ventro_testing.model.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public class SecurityUtils {

    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
    public static UUID getCurrentUserId() {
        return getCurrentUser().getUserId();
    }
}
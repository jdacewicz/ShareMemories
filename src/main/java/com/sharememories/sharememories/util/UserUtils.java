package com.sharememories.sharememories.util;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.exception.NotAuthenticatedException;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UserUtils {

    public static User getLoggedUser(SecurityUserDetailsService service) {
        Optional<User> user = service.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());

        return user.orElseThrow(NotAuthenticatedException::new);
    }
}

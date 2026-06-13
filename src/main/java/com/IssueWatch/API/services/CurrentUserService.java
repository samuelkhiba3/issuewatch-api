package com.IssueWatch.API.services;

import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new UnauthorizedException("User is not authenticated");
        }

        return user;
    }
}

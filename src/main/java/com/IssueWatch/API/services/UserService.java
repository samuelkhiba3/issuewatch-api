package com.IssueWatch.API.services;

import com.IssueWatch.API.dto.response.CurrentUserResponse;
import com.IssueWatch.API.entities.Role;
import com.IssueWatch.API.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final CurrentUserService currentUserService;

    public UserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public CurrentUserResponse getMe() {
        User user = currentUserService.getCurrentUser();

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .map(Enum::name)
                .toList();

        return new CurrentUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roles
        );
    }
}

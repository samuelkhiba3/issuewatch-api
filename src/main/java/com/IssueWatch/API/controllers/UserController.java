package com.IssueWatch.API.controllers;

import com.IssueWatch.API.dto.response.CurrentUserResponse;
import com.IssueWatch.API.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Profile", description = "My Profile endpoints")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getMe() {
        CurrentUserResponse response = userService.getMe();
        return ResponseEntity.ok(response);
    }
}

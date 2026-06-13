package com.IssueWatch.API.dto.response;

import com.IssueWatch.API.entities.Role;

import java.util.List;

public class CurrentUserResponse {

    private Long id;
    private String name;
    private String email;
    private List<String> roles;

    public CurrentUserResponse(Long id, String name, String email, List<String> roles){
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}

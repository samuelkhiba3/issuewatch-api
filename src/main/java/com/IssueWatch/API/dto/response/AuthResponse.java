package com.IssueWatch.API.dto.response;

public class AuthResponse {

    private String accessToken;
    private String type;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
        this.type = "Bearer";
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getType() {
        return type;
    }
}

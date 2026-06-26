package com.IssueWatch.API.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AssignIssuesRequest {

    @NotBlank(message = "Support user ID is required")
    private Long supportUserId;

    public Long getSupportUserId() {
        return supportUserId;
    }
}

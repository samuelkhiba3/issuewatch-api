package com.IssueWatch.API.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignIssuesRequest {

    @NotNull(message = "Support user ID is required")
    private Long supportUserId;

    public Long getSupportUserId() {
        return supportUserId;
    }
}

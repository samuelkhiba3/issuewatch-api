package com.IssueWatch.API.dto.request;

import com.IssueWatch.API.enums.IssueStatus;
import jakarta.validation.constraints.NotBlank;

public class UpdateIssueStatusRequest {

    @NotBlank(message = "Status is required")
    private IssueStatus status;

    public IssueStatus getStatus() {
        return status;
    }
}

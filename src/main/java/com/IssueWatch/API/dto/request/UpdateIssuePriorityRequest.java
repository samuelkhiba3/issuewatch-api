package com.IssueWatch.API.dto.request;

import com.IssueWatch.API.enums.IssuePriority;
import jakarta.validation.constraints.NotBlank;

public class UpdateIssuePriorityRequest {

    @NotBlank(message = "Priority is required")
    private IssuePriority priority;

    public IssuePriority getPriority() {
        return priority;
    }
}

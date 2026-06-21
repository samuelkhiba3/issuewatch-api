package com.IssueWatch.API.dto.request;

import com.IssueWatch.API.enums.IssuePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateIssueRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Affected system is required")
    @Size(max = 100, message = "Affected system cannot exceed 100 characters")
    private String affectedSystem;

    @NotNull(message = "Priority is required")
    private IssuePriority priority;

    public IssuePriority getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public String getAffectedSystem() {
        return affectedSystem;
    }

    public String getTitle() {
        return title;
    }
}

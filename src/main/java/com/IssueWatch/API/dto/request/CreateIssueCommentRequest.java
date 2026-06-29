package com.IssueWatch.API.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateIssueCommentRequest {

    @NotBlank(message = "comment is required")
    private String comment;

    private boolean internal = false;

    public String getComment() {
        return comment;
    }

    public boolean isInternal() {
        return internal;
    }
}

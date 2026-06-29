package com.IssueWatch.API.dto.response;

import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.entities.User;

import java.time.LocalDateTime;

public class IssueCommentResponse {

    private Long id;
    private Long userId;
    private Long issueId;
    private String userName;
    private String comment;
    private boolean internal;
    private LocalDateTime createdAt;

    public IssueCommentResponse(
            Long id,
            Long userId,
            Long issueId,
            String userName,
            String comment,
            boolean internal,
            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.issueId = issueId;
        this. userName = userName;
        this.comment = comment;
        this.internal = internal;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public String getUserName() {
        return userName;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isInternal() {
        return internal;
    }
}

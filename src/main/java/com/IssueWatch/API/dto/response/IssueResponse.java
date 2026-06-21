package com.IssueWatch.API.dto.response;

import com.IssueWatch.API.enums.IssuePriority;
import com.IssueWatch.API.enums.IssueStatus;

import java.time.LocalDateTime;

public class IssueResponse {

    private Long id;
    private String title;
    private String description;
    private String affectedSystem;
    private IssuePriority priority;
    private IssueStatus status;

    private Long reportedById;
    private String reportedByName;

    private Long assignedToId;
    private String assignedToName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    public IssueResponse(Long id,
                         String title,
                         String description,
                         String affectedSystem,
                         IssuePriority priority,
                         IssueStatus status,
                         Long reportedById,
                         String reportedByName,
                         Long assignedToId,
                         String assignedToName,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt,
                         LocalDateTime resolvedAt,
                         LocalDateTime closedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.affectedSystem = affectedSystem;
        this.priority = priority;
        this.status = status;
        this.reportedById = reportedById;
        this.reportedByName = reportedByName;
        this.assignedToId = assignedToId;
        this.assignedToName = assignedToName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
        this.closedAt = closedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAffectedSystem() {
        return affectedSystem;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public Long getReportedById() {
        return reportedById;
    }

    public String getReportedByName() {
        return reportedByName;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }
}

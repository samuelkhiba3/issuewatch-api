package com.IssueWatch.API.entities;

import com.IssueWatch.API.enums.IssuePriority;
import com.IssueWatch.API.enums.IssueStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String affectedSystem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssuePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status = IssueStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_user_id", nullable = false)
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    private LocalDateTime closedAt;

    public Issue() {
    }

    public Issue(String title, String description, String affectedSystem, IssuePriority priority, User reportedBy) {
        this.title = title;
        this.description = description;
        this.affectedSystem = affectedSystem;
        this.priority = priority;
        this.reportedBy = reportedBy;
        this.status = IssueStatus.OPEN;
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

    public IssueStatus getStatus() {
        return status;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public String getAffectedSystem() {
        return affectedSystem;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    @PrePersist
    public void beforeCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();

    }

    public void changeStatus(IssueStatus status) {
        this.status = status;

        if (status == IssueStatus.RESOLVED) {
            this.resolvedAt = LocalDateTime.now();
        }

        if (status == IssueStatus.CLOSED) {
            this.closedAt = LocalDateTime.now();
        }
    }

    public void assignTo(User supportUser) {
        this.assignedTo = supportUser;
        this.status = IssueStatus.ASSIGNED;
    }

    public void changePriority(IssuePriority priority) {
        this.priority = priority;
    }
}

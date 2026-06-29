package com.IssueWatch.API.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.Internal;

import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;

@Entity
@Table(name = "issue_comments")
public class IssueComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private boolean internal;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public IssueComment(){
    }

    public IssueComment(Issue issue, User user, String comment, boolean internal) {
        this.issue = issue;
        this.user = user;
        this.comment = comment;
        this.internal = internal;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Issue getIssue() {
        return issue;
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

    @PrePersist
    public void beforeUpdate() {
        this.createdAt = LocalDateTime.now();
    }
}

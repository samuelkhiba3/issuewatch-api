package com.IssueWatch.API.entities;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "issue_attachments")
public class IssueAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedBy;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true)
    private String storedFileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public IssueAttachment(){
    }

    public IssueAttachment(
            Issue issue,
            User uploadedBy,
            String originalFileName,
            String storedFileName,
            Long fileSize,
            String contentType,
            String filePath
    ) {
        this.issue = issue;
        this.uploadedBy = uploadedBy;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public Issue getIssue() {
        return issue;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    public void beforeCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

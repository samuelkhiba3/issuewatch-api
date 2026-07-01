package com.IssueWatch.API.dto.response;

import java.time.LocalDateTime;

public class IssueAttachmentResponse {

    private Long id;
    private Long issueId;
    private Long uploadedById;
    private String uploadedByName;
    private String originalFileName;
    private String contentType;
    private Long size;
    private LocalDateTime createAt;

    public IssueAttachmentResponse(
            Long id,
            Long issueId,
            Long uploadedById,
            String uploadedByName,
            String originalFileName,
            String contentType,
            Long size,
            LocalDateTime createAt
    ) {
        this.id = id;
        this.issueId = issueId;
        this.uploadedById = uploadedById;
        this.uploadedByName = uploadedByName;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.size = size;
        this.createAt = createAt;
    }

    public Long getId() {
        return id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public Long getUploadedById() {
        return uploadedById;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}

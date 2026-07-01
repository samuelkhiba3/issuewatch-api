package com.IssueWatch.API.services;

import com.IssueWatch.API.dto.response.IssueAttachmentResponse;
import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.entities.IssueAttachment;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.exceptions.ForbiddenException;
import com.IssueWatch.API.exceptions.ResourceNotFoundException;
import com.IssueWatch.API.repositories.IssueAttachmentRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Service
public class IssueAttachmentService {

    private final IssueAttachmentRepository issueAttachmentRepository;
    private final CurrentUserService currentUserService;
    private final IssueService issueService;
    private final FileStorageService fileStorageService;

    public IssueAttachmentService(
            IssueAttachmentRepository issueAttachmentRepository,
            CurrentUserService currentUserService,
            IssueService issueService,
            FileStorageService fileStorageService
    ) {
        this.issueService = issueService;
        this.currentUserService = currentUserService;
        this.issueAttachmentRepository = issueAttachmentRepository;
        this.fileStorageService = fileStorageService;
    }

    private IssueAttachmentResponse mapToResponse(IssueAttachment issueAttachment) {
        return new IssueAttachmentResponse(
                issueAttachment.getId(),
                issueAttachment.getIssue().getId(),
                issueAttachment.getUploadedBy().getId(),
                issueAttachment.getUploadedBy().getName(),
                issueAttachment.getOriginalFileName(),
                issueAttachment.getContentType(),
                issueAttachment.getFileSize(),
                issueAttachment.getCreatedAt()
        );
    }

    public IssueAttachmentResponse uploadAttachment(Long issueId, MultipartFile file) {
        User currentUser = currentUserService.getCurrentUser();

        Issue issue = issueService.findIssueOrThrow(issueId);

        if (!issueService.canViewIssue(issue, currentUser)) {
            throw new ForbiddenException("You do not have permission to upload attachment for this issue");
        }

        FileStorageService.StoredFile storedFile = fileStorageService.storeIssueAttachment(file);

        IssueAttachment savedAttachment = issueAttachmentRepository.save(
                new IssueAttachment(
                        issue,
                        currentUser,
                        storedFile.originalFileName(),
                        storedFile.storedFileName(),
                        storedFile.size(),
                        storedFile.contentType(),
                        storedFile.filePath()
                )
        );

        return mapToResponse(savedAttachment);
    }

    public List<IssueAttachmentResponse> getAttachments(Long issueId) {
        User currentUser = currentUserService.getCurrentUser();

        Issue issue = issueService.findIssueOrThrow(issueId);

        if (!issueService.canViewIssue(issue, currentUser)) {
            throw new ForbiddenException("You do not have permission to view attachments for this issue");
        }

        List<IssueAttachment> attachments = issueAttachmentRepository.findByIssueIdOrderByCreatedAtAsc(issueId);

        return attachments
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public record AttachmentDownload(
            Resource resource,
            String originalFileName,
            String contentType
    ){};

    public AttachmentDownload downloadAttachment(Long issueId, Long attachmentId) {
        User currentUser = currentUserService.getCurrentUser();

        Issue issue = issueService.findIssueOrThrow(issueId);

        if (!issueService.canViewIssue(issue, currentUser)) {
            throw new ForbiddenException("You do not have permission to download attachment for this issue");
        }

        IssueAttachment attachment = issueAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        if (!attachment.getIssue().getId().equals(issueId)) {
            throw new ResourceNotFoundException("Attachment not found");
        }

        Path filePath = fileStorageService.loadFile(attachment.getFilePath());

        try{
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not found");
            }

            return new AttachmentDownload(
                    resource,
                    attachment.getOriginalFileName(),
                    attachment.getContentType()
            );
        } catch (MalformedURLException exception) {
            throw new ResourceNotFoundException("File not found");
        }
    }
}

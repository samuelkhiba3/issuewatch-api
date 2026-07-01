package com.IssueWatch.API.controllers;

import com.IssueWatch.API.dto.response.IssueAttachmentResponse;
import com.IssueWatch.API.services.IssueAttachmentService;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/api/issues/{issueId}/attachments")
public class IssueAttachmentController {

    private final IssueAttachmentService issueAttachmentService;

    public IssueAttachmentController(IssueAttachmentService issueAttachmentService) {
        this.issueAttachmentService = issueAttachmentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IssueAttachmentResponse> uploadAttachment(
            @PathVariable Long issueId,
            @NotNull @RequestParam("file")MultipartFile file
            ){
        IssueAttachmentResponse response = issueAttachmentService.uploadAttachment(issueId, file);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping()
    public ResponseEntity<List<IssueAttachmentResponse>>  getAttachments(
            @PathVariable Long issueId
    ) {
        List<IssueAttachmentResponse> response = issueAttachmentService.getAttachments(issueId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long issueId,
            @PathVariable Long attachmentId
    ) {
        IssueAttachmentService.AttachmentDownload download = issueAttachmentService
                .downloadAttachment(issueId, attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + download.originalFileName() + "\""
                )
                .body(download.resource());
    }
}

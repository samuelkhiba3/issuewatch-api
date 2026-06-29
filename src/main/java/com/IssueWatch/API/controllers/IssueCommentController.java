package com.IssueWatch.API.controllers;

import com.IssueWatch.API.dto.request.CreateIssueCommentRequest;
import com.IssueWatch.API.dto.response.IssueCommentResponse;
import com.IssueWatch.API.services.IssueCommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/issues/{issueId}/comments")
public class IssueCommentController {

    private final IssueCommentService issueCommentService;

    public IssueCommentController(IssueCommentService issueCommentService) {
        this.issueCommentService = issueCommentService;
    }

    @PostMapping
    public ResponseEntity<IssueCommentResponse> addComment(@PathVariable Long issueId, @Valid @RequestBody CreateIssueCommentRequest request) {
        IssueCommentResponse response = issueCommentService.addComment(issueId, request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<IssueCommentResponse>> getComments(@PathVariable Long issueId) {
        List<IssueCommentResponse> response = issueCommentService.getComments(issueId);

        return ResponseEntity.ok(response);
    }
}

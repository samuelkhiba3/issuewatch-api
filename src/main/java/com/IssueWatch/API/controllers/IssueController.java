package com.IssueWatch.API.controllers;

import com.IssueWatch.API.dto.request.AssignIssuesRequest;
import com.IssueWatch.API.dto.request.CreateIssueRequest;
import com.IssueWatch.API.dto.request.UpdateIssuePriorityRequest;
import com.IssueWatch.API.dto.request.UpdateIssueStatusRequest;
import com.IssueWatch.API.dto.response.IssueResponse;
import com.IssueWatch.API.dto.response.PagedResponse;
import com.IssueWatch.API.enums.IssuePriority;
import com.IssueWatch.API.enums.IssueStatus;
import com.IssueWatch.API.services.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody CreateIssueRequest request ) {
        IssueResponse response = issueService.createIssue(request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<IssueResponse>> getMyIssues() {
        List<IssueResponse> responses = issueService.getMyIssues();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long id) {
        IssueResponse response = issueService.getIssueById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<IssueResponse>> getAllIssues(
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) IssuePriority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        PagedResponse<IssueResponse> response = issueService.getAllIssues(
                status,
                priority,
                page,
                size,
                sortBy,
                direction);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<IssueResponse> assignIssue(@PathVariable Long id, @Valid @RequestBody AssignIssuesRequest request) {
        IssueResponse response = issueService.assignIssue(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IssueResponse> updateIssueStatus(@PathVariable Long id, @RequestBody UpdateIssueStatusRequest request) {
        IssueResponse response = issueService.updateIssueStatus(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<IssueResponse> updateIssuePriority(@PathVariable Long id, @RequestBody UpdateIssuePriorityRequest request) {
        IssueResponse response = issueService.updateIssuePriority(id, request);

        return ResponseEntity.ok(response);
    }
}

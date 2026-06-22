package com.IssueWatch.API.controllers;

import com.IssueWatch.API.dto.request.CreateIssueRequest;
import com.IssueWatch.API.dto.response.IssueResponse;
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
}

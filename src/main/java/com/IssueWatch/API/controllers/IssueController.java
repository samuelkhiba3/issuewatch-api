package com.IssueWatch.API.controllers;

import com.IssueWatch.API.dto.request.CreateIssueRequest;
import com.IssueWatch.API.dto.response.IssueResponse;
import com.IssueWatch.API.services.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

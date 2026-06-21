package com.IssueWatch.API.services;

import com.IssueWatch.API.dto.request.CreateIssueRequest;
import com.IssueWatch.API.dto.response.IssueResponse;
import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.repositories.IssueRepository;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final CurrentUserService currentUserService;

    public IssueService(IssueRepository issueRepository, CurrentUserService currentUserService) {
        this.issueRepository = issueRepository;
        this.currentUserService = currentUserService;
    }

    public IssueResponse createIssue(CreateIssueRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Issue issue = new Issue(
                request.getTitle(),
                request.getDescription(),
                request.getAffectedSystem(),
                request.getPriority(),
                currentUser
        );

        Issue savedIssue = issueRepository.save(issue);

        return mapToResponse(savedIssue);
    }

    private IssueResponse mapToResponse(Issue savedIssue) {
        Long assignedToId = null;
        String assignedToName = null;

        if (savedIssue.getAssignedTo() != null) {
            assignedToId = savedIssue.getAssignedTo().getId();
            assignedToName = savedIssue.getAssignedTo().getName();
        }

        return new IssueResponse(
                savedIssue.getId(),
                savedIssue.getTitle(),
                savedIssue.getDescription(),
                savedIssue.getAffectedSystem(),
                savedIssue.getPriority(),
                savedIssue.getStatus(),
                savedIssue.getReportedBy().getId(),
                savedIssue.getReportedBy().getName(),
                assignedToId,
                assignedToName,
                savedIssue.getCreatedAt(),
                savedIssue.getUpdatedAt(),
                savedIssue.getResolvedAt(),
                savedIssue.getClosedAt()
        );
    }


}

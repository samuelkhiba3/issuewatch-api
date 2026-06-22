package com.IssueWatch.API.services;

import com.IssueWatch.API.dto.request.CreateIssueRequest;
import com.IssueWatch.API.dto.response.IssueResponse;
import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.entities.Role;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.enums.IssuePriority;
import com.IssueWatch.API.enums.IssueStatus;
import com.IssueWatch.API.enums.RoleName;
import com.IssueWatch.API.exceptions.ForbiddenException;
import com.IssueWatch.API.exceptions.ResourceNotFoundException;
import com.IssueWatch.API.exceptions.UnauthorizedException;
import com.IssueWatch.API.repositories.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<IssueResponse> getMyIssues() {
        User user = currentUserService.getCurrentUser();

        List<Issue> issues = issueRepository.findByReportedById(user.getId());

        return issues.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private boolean hasRole(User user, RoleName roleName) {
        return user.getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch(role -> role == roleName);
    }

    private boolean canViewIssue(Issue issue, User user) {
        boolean isReporter = issue
                .getReportedBy()
                .getId()
                .equals(user.getId());

        boolean isAssignedSupport = issue.getAssignedTo() != null &&
                issue.getAssignedTo().getId()
                        .equals(user.getId());

        boolean isSupportOrAdmin = hasRole(user, RoleName.ADMIN) || hasRole(user, RoleName.SUPPORT);

        return isSupportOrAdmin || isAssignedSupport || isReporter;
    }

    public IssueResponse getIssueById(Long issueId) {
        User currentUser = currentUserService.getCurrentUser();

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        if (!canViewIssue(issue, currentUser)) {
            throw new ForbiddenException("You do not have permission to view this issue");
        }

        return mapToResponse(issue);
    }

    public List<IssueResponse> getAllIssues(IssueStatus status, IssuePriority priority) {
        User currentUser = currentUserService.getCurrentUser();

        if (!hasRole(currentUser, RoleName.ADMIN) && !hasRole(currentUser, RoleName.SUPPORT)) {
            throw new ForbiddenException("You do not have permission to view all issues");
        }

        List<Issue> issues;

        if (status !=null && priority != null) {
            issues = issueRepository.findByStatusAndPriority(status, priority);
        } else if (status != null) {
            issues = issueRepository.findByStatus(status);
        } else if (priority != null) {
            issues = issueRepository.findByPriority(priority);
        } else {
            issues = issueRepository.findAll();
        }

        return issues.stream()
                .map(this::mapToResponse)
                .toList();
    }
}

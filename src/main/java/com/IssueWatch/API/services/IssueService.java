package com.IssueWatch.API.services;

import com.IssueWatch.API.dto.request.AssignIssuesRequest;
import com.IssueWatch.API.dto.request.CreateIssueRequest;
import com.IssueWatch.API.dto.request.UpdateIssuePriorityRequest;
import com.IssueWatch.API.dto.request.UpdateIssueStatusRequest;
import com.IssueWatch.API.dto.response.IssueResponse;
import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.entities.Role;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.enums.IssuePriority;
import com.IssueWatch.API.enums.IssueStatus;
import com.IssueWatch.API.enums.RoleName;
import com.IssueWatch.API.exceptions.BadRequestException;
import com.IssueWatch.API.exceptions.ForbiddenException;
import com.IssueWatch.API.exceptions.ResourceNotFoundException;
import com.IssueWatch.API.repositories.IssueRepository;
import com.IssueWatch.API.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public IssueService(IssueRepository issueRepository, CurrentUserService currentUserService, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
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

    public Issue findIssueOrThrow(Long issueId) {

        return issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
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

    public boolean hasRole(User user, RoleName roleName) {
        return user.getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch(role -> role == roleName);
    }

    public boolean canViewIssue(Issue issue, User user) {
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

        Issue issue = findIssueOrThrow(issueId);

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

    @Transactional
    public IssueResponse assignIssue(Long issueId, AssignIssuesRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        if (!hasRole(currentUser, RoleName.ADMIN)) {
            throw new ForbiddenException("Only ADMIN can assign issues");
        }

        User assignedUserSupport = userRepository.findById(request.getSupportUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Support user not found"));

        if (!hasRole(assignedUserSupport, RoleName.SUPPORT)) {
            throw new BadRequestException("Assigned user must have SUPPORT role");
        }

        Issue issue = findIssueOrThrow(issueId);

        issue.assignTo(assignedUserSupport);

        Issue savedIssue = issueRepository.save(issue);

        return mapToResponse(savedIssue);
    }

    @Transactional
    public IssueResponse updateIssueStatus(Long issueId, UpdateIssueStatusRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        if (!hasRole(currentUser, RoleName.SUPPORT) && !hasRole(currentUser, RoleName.ADMIN)) {
            throw new ForbiddenException("Only ADMIN or SUPPORT can update the issue status");
        }

        Issue issue = findIssueOrThrow(issueId);

        if (hasRole(currentUser, RoleName.SUPPORT)
                && issue.getAssignedTo() != null
                && !issue.getAssignedTo().getId().equals(currentUser.getId())
                && !hasRole(currentUser, RoleName.ADMIN)
        ) {
            throw new ForbiddenException("You can only update the issues assigned to you");
        }

        issue.changeStatus(request.getStatus());

        issueRepository.save(issue);

        return mapToResponse(issue);
    }

    @Transactional
    public IssueResponse updateIssuePriority(Long issueId, UpdateIssuePriorityRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        if (!hasRole(currentUser, RoleName.ADMIN)) {
            throw new ForbiddenException("Only ADMIN can change the issue priority");
        }

        Issue issue = findIssueOrThrow(issueId);

        issue.changePriority(request.getPriority());

        issueRepository.save(issue);

        return mapToResponse(issue);
    }

}

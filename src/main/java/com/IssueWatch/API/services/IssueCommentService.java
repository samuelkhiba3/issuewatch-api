package com.IssueWatch.API.services;

import com.IssueWatch.API.dto.request.CreateIssueCommentRequest;
import com.IssueWatch.API.dto.response.IssueCommentResponse;
import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.entities.IssueComment;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.enums.RoleName;
import com.IssueWatch.API.exceptions.ForbiddenException;
import com.IssueWatch.API.repositories.IssueCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueCommentService {

    private final IssueCommentRepository issueCommentRepository;
    private final IssueService issueService;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    public IssueCommentService(IssueService issueService, IssueCommentRepository issueCommentRepository, CurrentUserService currentUserService, NotificationService notificationService) {
        this.currentUserService = currentUserService;
        this.issueService = issueService;
        this.issueCommentRepository = issueCommentRepository;
        this.notificationService = notificationService;
    }

    private IssueCommentResponse mapToResponse(IssueComment comment) {
        return new IssueCommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getIssue().getId(),
                comment.getUser().getName(),
                comment.getComment(),
                comment.isInternal(),
                comment.getCreatedAt()
        );
    }

    public IssueCommentResponse addComment(Long issueId, CreateIssueCommentRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Issue issue = issueService.findIssueOrThrow(issueId);

        if (!issueService.canViewIssue(issue, currentUser)) {
            throw new ForbiddenException("You do not have permission to comment on this issue");
        }

        if (request.isInternal()
                && !issueService.hasRole(currentUser, RoleName.ADMIN)
                && !issueService.hasRole(currentUser, RoleName.SUPPORT)
        ) {
            throw new ForbiddenException("Only ADMIN or SUPPORT can add internal comments");
        }

        IssueComment issueComment = new IssueComment(
                issue,
                currentUser,
                request.getComment(),
                request.isInternal()
        );

        IssueComment savedComment = issueCommentRepository.save(issueComment);

        if (!savedComment.isInternal()) {
            notificationService.notifyNewComment(issue, currentUser);
        }

        return mapToResponse(savedComment);
    }

    public List<IssueCommentResponse> getComments(Long issueId) {
        User currentUser = currentUserService.getCurrentUser();

        Issue issue = issueService.findIssueOrThrow(issueId);

        if (!issueService.canViewIssue(issue, currentUser)) {
            throw new ForbiddenException("You don't have permission to view comments for this issue");
        }

        List<IssueComment> comments;

        if (issueService.hasRole(currentUser, RoleName.SUPPORT)
                || issueService.hasRole(currentUser, RoleName.ADMIN)
        ) {
            comments = issueCommentRepository.findByIssueIdOrderByCreatedAtAsc(issueId);
        } else {
            comments = issueCommentRepository.findByIssueIdAndInternalFalseOrderByCreatedAtAsc(issueId);
        }

        return comments.stream()
                .map(this::mapToResponse)
                .toList();
    }
}

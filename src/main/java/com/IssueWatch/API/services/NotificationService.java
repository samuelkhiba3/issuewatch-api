package com.IssueWatch.API.services;

import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.entities.User;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void notifyIssueCreated(Issue issue) {
        User reporter = issue.getReportedBy();

        String subject = "Issue created: " + issue.getTitle();

        String body = """
                Hi %s,
                
                Your issue has been created successfully
                
                Issue: %s
                Affected system: %s
                Priority: %s
                Status: %s
                
                Regards,
                IssueWatch
                """
                .formatted(
                        reporter.getName(),
                        issue.getTitle(),
                        issue.getAffectedSystem(),
                        issue.getPriority(),
                        issue.getStatus()
                );


        emailService.sendEmail(
                reporter.getEmail(),
                subject,
                body
        );
    }

    public void notifyIssueAssigned(Issue issue) {
        if (issue.getAssignedTo() == null ) {
            return;
        }

        User supportUser = issue.getAssignedTo();

        String subject = "Issue assigned: " + issue.getTitle();

        String body = """
                Hi %s,
                
                A new issue has been assigned to you.
                
                Issue: %s
                System affected: %s
                Priority: %s
                Status: %s
                Reported by: %s
                
                Regards,
                IssueWatch
                """
                .formatted(
                        supportUser.getName(),
                        issue.getTitle(),
                        issue.getAffectedSystem(),
                        issue.getPriority(),
                        issue.getStatus(),
                        issue.getReportedBy()
                                .getName()
                );

        emailService.sendEmail(
                supportUser.getEmail(),
                subject,
                body
        );
    }

    public void notifyIssueStatusChanged(Issue issue) {
        User reporter = issue.getReportedBy();

        String subject = "Issue status updated: " + issue.getTitle();

        String body = """
                Hi %s
                
                Your issue status has been updated.
                
                Issue: %s
                System affected: %s
                Priority: %s
                Status: %s
                
                Regards,
                IssueWatch
                """
                .formatted(
                        reporter.getName(),
                        issue.getTitle(),
                        issue.getAffectedSystem(),
                        issue.getPriority(),
                        issue.getStatus()
                );

        emailService.sendEmail(
                reporter.getEmail(),
                subject,
                body
        );
    }

    public void notifyNewComment(Issue issue, User commentAuthor) {
        User reporter = issue.getReportedBy();

        if (reporter.getId().equals(commentAuthor.getId())) {
            return;
        }

        String subject = "New comment on issue: " + issue.getTitle();

        String body = """
                Hi %s,

                A new comment was added to your issue.

                Issue: %s
                Comment added by: %s
                Current status: %s

                Regards,
                IssueWatch
                """
                .formatted(
                    reporter.getName(),
                    issue.getTitle(),
                    commentAuthor.getName(),
                    issue.getStatus()
                );

        emailService.sendEmail(
                reporter.getEmail(),
                subject,
                body
        );
    }
}

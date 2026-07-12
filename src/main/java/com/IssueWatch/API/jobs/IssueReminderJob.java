package com.IssueWatch.API.jobs;

import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.repositories.IssueRepository;
import com.IssueWatch.API.services.NotificationService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IssueReminderJob {

    private static final Logger logger = LoggerFactory.getLogger(IssueReminderJob.class);

    private final IssueRepository issueRepository;
    private final NotificationService notificationService;

    @Value("${jobs.enabled:true}")
    private boolean jobsEnabled;

    public IssueReminderJob(IssueRepository issueRepository, NotificationService notificationService) {
        this.issueRepository = issueRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${jobs.issue-reminder-ms:3600000}")
    public void sendUnresolvedIssueReminder() {
        if (!jobsEnabled) {
            return;
        }

        List<Issue> unresolvedIssues = issueRepository.findUnresolvedHighPriorityIssues();

        for (Issue issue : unresolvedIssues) {
            notificationService.notifyUnresolvedIssueReminder(issue);
        }

        logger.info(
                "Unresolved high/critical issue reminder job completed. Issues checked: {}",
                unresolvedIssues.size()
        );
    }
}

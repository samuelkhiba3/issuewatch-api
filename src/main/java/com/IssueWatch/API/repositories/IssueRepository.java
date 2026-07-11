package com.IssueWatch.API.repositories;

import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.enums.IssuePriority;
import com.IssueWatch.API.enums.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByReportedById(Long reportedByUserId);

    List<Issue> findByAssignedToId(Long assignedToUserId);

    List<Issue> findByStatus(IssueStatus status);

    List<Issue> findByPriority(IssuePriority priority);

    List<Issue> findByStatusAndPriority(
            IssueStatus status,
            IssuePriority priority
    );

    Page<Issue> findByStatus(IssueStatus status, Pageable pageable);

    Page<Issue> findByPriority(IssuePriority priority, Pageable pageable);

    Page<Issue> findByStatusAndPriority(
            IssueStatus status,
            IssuePriority priority,
            Pageable pageable
    );
}
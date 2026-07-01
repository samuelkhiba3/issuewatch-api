package com.IssueWatch.API.repositories;

import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.enums.IssuePriority;
import com.IssueWatch.API.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    public List<Issue> findByReportedById(Long reportedByUserId);

    public List<Issue> findByAssignedToId(Long assignedToUserId);

    public List<Issue> findByStatus(IssueStatus status);

    public List<Issue> findByPriority(IssuePriority priority);

    public List<Issue> findByStatusAndPriority(IssueStatus status, IssuePriority priority);

}

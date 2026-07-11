package com.IssueWatch.API.repositories;

import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.enums.IssuePriority;
import com.IssueWatch.API.enums.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
        SELECT i
        FROM Issue i
        WHERE (:status IS NULL OR i.status = :status)
          AND (:priority IS NULL OR i.priority = :priority)
          AND (:affectedSystem IS NULL OR LOWER(i.affectedSystem) LIKE LOWER(CONCAT('%', :affectedSystem, '%')))
          AND (:reportedByUserId IS NULL OR i.reportedBy.id = :reportedByUserId)
          AND (:assignedToUserId IS NULL OR i.assignedTo.id = :assignedToUserId)
          AND (
                :keyword IS NULL
                OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(i.affectedSystem) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        """)
    Page<Issue> searchIssues(
            @Param("status") IssueStatus status,
            @Param("priority") IssuePriority priority,
            @Param("affectedSystem") String affectedSystem,
            @Param("reportedByUserId") Long reportedByUserId,
            @Param("assignedToUserId") Long assignedToUserId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
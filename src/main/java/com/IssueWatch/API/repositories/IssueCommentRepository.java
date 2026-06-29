package com.IssueWatch.API.repositories;

import com.IssueWatch.API.entities.IssueComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueCommentRepository extends JpaRepository<IssueComment, Long> {
    List<IssueComment> findByIssueIdOrderByCreatedAtAsc(Long issueId);

    List<IssueComment> findByIssueIdAndInternalFalseOrderByCreatedAtAsc(Long issueId);
}

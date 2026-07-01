package com.IssueWatch.API.repositories;

import com.IssueWatch.API.entities.IssueAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueAttachmentRepository extends JpaRepository<IssueAttachment, Long> {

    public List<IssueAttachment> findByIssueIdOrderByCreatedAtAsc(Long issueId);
}

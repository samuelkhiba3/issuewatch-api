package com.IssueWatch.API.repositories;

import com.IssueWatch.API.entities.Role;
import com.IssueWatch.API.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName role);
}

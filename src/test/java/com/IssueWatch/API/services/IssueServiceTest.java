package com.IssueWatch.API.services;

import com.IssueWatch.API.dto.request.AssignIssuesRequest;
import com.IssueWatch.API.dto.response.IssueResponse;
import com.IssueWatch.API.entities.Issue;
import com.IssueWatch.API.entities.Role;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.enums.IssuePriority;
import com.IssueWatch.API.enums.RoleName;
import com.IssueWatch.API.exceptions.BadRequestException;
import com.IssueWatch.API.exceptions.ForbiddenException;
import com.IssueWatch.API.repositories.IssueRepository;
import com.IssueWatch.API.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    private IssueService issueService;

    private Role adminRole;
    private Role supportRole;
    private Role userRole;

    private User adminUser;
    private User supportUser;
    private User normalUser;
    private Issue issue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        issueService = new IssueService(
                issueRepository,
                currentUserService,
                userRepository,
                notificationService
        );

        adminRole = new Role(RoleName.ADMIN);
        supportRole = new Role(RoleName.SUPPORT);
        userRole = new Role(RoleName.USER);

        adminUser = new User(
                "Admin User",
                "admin@example.com",
                "password",
                Set.of(adminRole)
        );

        supportUser = new User(
                "Support User",
                "support@example.com",
                "password",
                Set.of(supportRole)
        );

        normalUser = new User(
                "Normal User",
                "user@example.com",
                "password",
                Set.of(userRole)
        );

        issue = new Issue(
                "Login page is down",
                "Users cannot access the login page.",
                "Authentication Portal",
                IssuePriority.HIGH,
                normalUser
        );
    }

    @Test
    void adminCanAssignIssueToSupportUser() {
        AssignIssuesRequest request = mock(AssignIssuesRequest.class);

        when(request.getSupportUserId()).thenReturn(2L);
        when(currentUserService.getCurrentUser()).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(supportUser));
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(issueRepository.save(issue)).thenReturn(issue);

        IssueResponse response = issueService.assignIssue(1L, request);

        assertThat(response.getAssignedToName()).isEqualTo("Support User");
        assertThat(response.getStatus().name()).isEqualTo("ASSIGNED");

        verify(issueRepository).save(issue);
        verify(notificationService).notifyIssueAssigned(issue);
    }

    @Test
    void nonAdminCannotAssignIssue() {
        AssignIssuesRequest request = mock(AssignIssuesRequest.class);

        when(currentUserService.getCurrentUser()).thenReturn(normalUser);

        assertThatThrownBy(() -> issueService.assignIssue(1L, request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Only ADMIN can assign issues");

        verify(issueRepository, never()).save(any());
        verify(notificationService, never()).notifyIssueAssigned(any());
    }

    @Test
    void assignedUserMustHaveSupportRole() {
        AssignIssuesRequest request = mock(AssignIssuesRequest.class);

        when(request.getSupportUserId()).thenReturn(3L);
        when(currentUserService.getCurrentUser()).thenReturn(adminUser);
        when(userRepository.findById(3L)).thenReturn(Optional.of(normalUser));

        assertThatThrownBy(() -> issueService.assignIssue(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Assigned user must have SUPPORT role");

        verify(issueRepository, never()).save(any());
        verify(notificationService, never()).notifyIssueAssigned(any());
    }
}
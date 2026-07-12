package com.IssueWatch.API.services;

import com.IssueWatch.API.dto.request.LoginRequest;
import com.IssueWatch.API.dto.request.RegisterRequest;
import com.IssueWatch.API.dto.response.AuthResponse;
import com.IssueWatch.API.dto.response.MessageResponse;
import com.IssueWatch.API.entities.RefreshToken;
import com.IssueWatch.API.entities.Role;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.enums.RoleName;
import com.IssueWatch.API.exceptions.BadRequestException;
import com.IssueWatch.API.repositories.RoleRepository;
import com.IssueWatch.API.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    private Role userRole;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authService = new AuthService(
                userRepository,
                roleRepository,
                passwordEncoder,
                jwtService,
                refreshTokenService
        );

        userRole = new Role(RoleName.USER);

        user = new User(
                "Lerato Khiba",
                "samuelkhiba3@gmail.com",
                "hashed-password",
                Set.of(userRole)
        );
    }

    @Test
    void registerCreatesUserSuccessfully() {
        RegisterRequest request = mock(RegisterRequest.class);

        when(request.getName()).thenReturn("Lerato Khiba");
        when(request.getEmail()).thenReturn("samuelkhiba3@gmail.com");
        when(request.getPassword()).thenReturn("password123");

        when(userRepository.existsByEmail("samuelkhiba3@gmail.com"))
                .thenReturn(false);

        when(roleRepository.findByName(RoleName.USER))
                .thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashed-password");

        MessageResponse response = authService.register(request);

        assertThat(response.getMessage())
                .isEqualTo("User registered successfully");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterRequest request = mock(RegisterRequest.class);

        when(request.getEmail()).thenReturn("samuelkhiba3@gmail.com");

        when(userRepository.existsByEmail("samuelkhiba3@gmail.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginSucceedsWithCorrectPassword() {
        LoginRequest request = mock(LoginRequest.class);

        when(request.getEmail()).thenReturn("samuelkhiba3@gmail.com");
        when(request.getPassword()).thenReturn("password123");

        when(userRepository.findByEmail("samuelkhiba3@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "hashed-password"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("access-token");

        RefreshToken refreshToken = new RefreshToken(
                "refresh-token",
                user,
                LocalDateTime.now().plusDays(7)
        );

        when(refreshTokenService.createRefreshToken(user))
                .thenReturn(refreshToken);

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getType()).isEqualTo("Bearer");

        verify(refreshTokenService).createRefreshToken(user);
    }

    @Test
    void loginRejectsWrongPassword() {
        LoginRequest request = mock(LoginRequest.class);

        when(request.getEmail()).thenReturn("samuelkhiba3@gmail.com");
        when(request.getPassword()).thenReturn("wrong-password");

        when(userRepository.findByEmail("samuelkhiba3@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong-password", "hashed-password"))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid email or password");

        verify(jwtService, never()).generateToken(any(User.class));
        verify(refreshTokenService, never()).createRefreshToken(any(User.class));
    }
}
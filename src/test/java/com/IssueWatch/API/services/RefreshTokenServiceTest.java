package com.IssueWatch.API.services;

import com.IssueWatch.API.entities.RefreshToken;
import com.IssueWatch.API.entities.Role;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.enums.RoleName;
import com.IssueWatch.API.exceptions.UnauthorizedException;
import com.IssueWatch.API.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken validRefreshToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        refreshTokenService = new RefreshTokenService(refreshTokenRepository);

        Role userRole = new Role(RoleName.USER);

        user = new User(
                "Lerato Khiba",
                "samuelkhiba3@gmail.com",
                "hashed-password",
                Set.of(userRole)
        );

        validRefreshToken = new RefreshToken(
                "valid-refresh-token",
                user,
                LocalDateTime.now().plusDays(7)
        );
    }

    @Test
    void createRefreshTokenCreatesAndSavesToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getToken()).isNotBlank();
        assertThat(result.isRevoked()).isFalse();
        assertThat(result.getExpiresAt()).isAfter(LocalDateTime.now());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void validateRefreshTokenReturnsTokenWhenValid() {
        when(refreshTokenRepository.findByToken("valid-refresh-token"))
                .thenReturn(Optional.of(validRefreshToken));

        RefreshToken result = refreshTokenService.validateRefreshToken("valid-refresh-token");

        assertThat(result).isEqualTo(validRefreshToken);
    }

    @Test
    void validateRefreshTokenRejectsInvalidToken() {
        when(refreshTokenRepository.findByToken("missing-token"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.validateRefreshToken("missing-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void validateRefreshTokenRejectsRevokedToken() {
        validRefreshToken.revoke();

        when(refreshTokenRepository.findByToken("valid-refresh-token"))
                .thenReturn(Optional.of(validRefreshToken));

        assertThatThrownBy(() -> refreshTokenService.validateRefreshToken("valid-refresh-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token has been revoked");
    }

    @Test
    void validateRefreshTokenRejectsExpiredToken() {
        RefreshToken expiredToken = new RefreshToken(
                "expired-refresh-token",
                user,
                LocalDateTime.now().minusDays(1)
        );

        when(refreshTokenRepository.findByToken("expired-refresh-token"))
                .thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> refreshTokenService.validateRefreshToken("expired-refresh-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token has expired");
    }

    @Test
    void revokeRefreshTokenRevokesAndSavesToken() {
        when(refreshTokenRepository.findByToken("valid-refresh-token"))
                .thenReturn(Optional.of(validRefreshToken));

        refreshTokenService.revokeRefreshToken("valid-refresh-token");

        assertThat(validRefreshToken.isRevoked()).isTrue();

        verify(refreshTokenRepository).save(validRefreshToken);
    }
}
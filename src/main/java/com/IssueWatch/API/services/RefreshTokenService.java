package com.IssueWatch.API.services;

import com.IssueWatch.API.entities.RefreshToken;
import com.IssueWatch.API.entities.User;
import com.IssueWatch.API.exceptions.UnauthorizedException;
import com.IssueWatch.API.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private String generateSecureToken() {
        byte randomBytes[] = new byte[64];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public RefreshToken createRefreshToken(User user) {
        String token = generateSecureToken();

        RefreshToken refreshToken = new RefreshToken(
                token,
                user,
                LocalDateTime.now().plusDays(7)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked())
            throw new UnauthorizedException("Refresh token has been revoked");

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new UnauthorizedException("Refresh token has expired");

        return refreshToken;
    }

    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        refreshToken.revoke();

        refreshTokenRepository.save(refreshToken);
    }

    public void revokeAllUserRefreshTokens(User user) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId());

        activeTokens.forEach(RefreshToken::revoke);

        refreshTokenRepository.saveAll(activeTokens);
    }
}

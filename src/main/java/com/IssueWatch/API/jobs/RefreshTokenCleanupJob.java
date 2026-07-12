package com.IssueWatch.API.jobs;

import com.IssueWatch.API.repositories.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RefreshTokenCleanupJob {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenCleanupJob.class);
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jobs.enabled:true}")
    private boolean jobsEnabled;

    public RefreshTokenCleanupJob(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${jobs.refresh-token-cleanup-ms:3600000}")
    public void cleanExpiredRefreshToken() {
        if (!jobsEnabled) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        refreshTokenRepository.deleteByExpiresAtBefore(now);

        logger.info("Expired refresh tokens cleaned at: {}", now);
    }
}

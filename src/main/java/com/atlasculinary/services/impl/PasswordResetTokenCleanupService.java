package com.atlasculinary.services.impl;

import com.atlasculinary.repositories.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenCleanupService {
    
    private static final Logger LOGGER = Logger.getLogger(PasswordResetTokenCleanupService.class.getName());
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            passwordResetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            LOGGER.info("Đã xóa các token reset password đã hết hạn");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi xóa token đã hết hạn: " + e.getMessage());
        }
    }
}
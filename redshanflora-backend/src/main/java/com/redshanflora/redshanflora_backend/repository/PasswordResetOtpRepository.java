package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.PasswordResetOtp;
import com.redshanflora.redshanflora_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findFirstByUserAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(User user, Instant now);

    Optional<PasswordResetOtp> findFirstByUserAndVerifiedTrueAndExpiresAtAfterOrderByCreatedAtDesc(User user, Instant now);

    Optional<PasswordResetOtp> findFirstByUserOrderByCreatedAtDesc(User user);

    void deleteByUser(User user);
}

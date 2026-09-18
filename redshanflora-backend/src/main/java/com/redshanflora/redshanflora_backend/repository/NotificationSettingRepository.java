package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.NotificationSetting;
import com.redshanflora.redshanflora_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUser(User user);
    Optional<NotificationSetting> findByUserId(Long userId);
}

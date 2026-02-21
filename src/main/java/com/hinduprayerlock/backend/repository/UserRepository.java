package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.model.dto.AdminUserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID>{

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
SELECT u.id, u.email, u.createdAt, COUNT(h.id)
FROM UserEntity u
LEFT JOIN UserPrayerHistory h ON u.id = h.userId
GROUP BY u.id, u.email, u.createdAt
""")
    List<Object[]> getUsersWithUsageRaw();
}

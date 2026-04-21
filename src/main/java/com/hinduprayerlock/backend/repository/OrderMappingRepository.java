package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.OrderMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderMappingRepository extends JpaRepository<OrderMapping, String> {

    Optional<OrderMapping> findByOrderId(String orderId);
}
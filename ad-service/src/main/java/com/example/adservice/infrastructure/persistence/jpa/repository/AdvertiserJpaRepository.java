package com.example.adservice.infrastructure.persistence.jpa.repository;

import com.example.adservice.infrastructure.persistence.jpa.entity.AdvertiserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdvertiserJpaRepository extends JpaRepository<AdvertiserEntity, UUID> {
    Optional<AdvertiserEntity> findByUsername(String username);
}

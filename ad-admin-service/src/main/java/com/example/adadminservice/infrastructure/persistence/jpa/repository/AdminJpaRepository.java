package com.example.adadminservice.infrastructure.persistence.jpa.repository;

import com.example.adadminservice.infrastructure.persistence.jpa.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminJpaRepository extends JpaRepository<AdminEntity, UUID> {
    Optional<AdminEntity> findByEmail(String email);
}

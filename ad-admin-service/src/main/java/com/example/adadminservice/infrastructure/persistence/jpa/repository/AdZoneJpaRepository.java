package com.example.adadminservice.infrastructure.persistence.jpa.repository;

import com.example.adadminservice.infrastructure.persistence.jpa.entity.AdZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdZoneJpaRepository extends JpaRepository<AdZoneEntity, UUID> {
}

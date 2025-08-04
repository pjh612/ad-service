package com.example.adservice.infrastructure.persistence.jpa.repository;

import com.example.adservice.infrastructure.persistence.jpa.entity.BidForAdZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BidForAdZoneJpaRepository extends JpaRepository<BidForAdZoneEntity, UUID> {
}

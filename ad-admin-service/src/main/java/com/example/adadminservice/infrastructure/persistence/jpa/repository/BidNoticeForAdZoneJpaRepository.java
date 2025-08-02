package com.example.adadminservice.infrastructure.persistence.jpa.repository;


import com.example.adadminservice.infrastructure.persistence.jpa.entity.BidNoticeForAdZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidNoticeForAdZoneJpaRepository extends JpaRepository<BidNoticeForAdZoneEntity, UUID> {
    List<BidNoticeForAdZoneEntity> findByZoneId(UUID zoneId);
}


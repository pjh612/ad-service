package com.example.adservice.domain.repository;

import com.example.adservice.domain.model.AdZone;

import java.util.Optional;
import java.util.UUID;

public interface AdZoneRepository {
    Optional<AdZone> findById(UUID id);

    AdZone save(AdZone adZone);
}

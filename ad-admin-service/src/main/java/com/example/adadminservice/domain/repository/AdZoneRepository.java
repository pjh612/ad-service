package com.example.adadminservice.domain.repository;

import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.model.AdZoneState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AdZoneRepository {
    Optional<AdZone> findById(UUID id);

    AdZone save(AdZone adZone);

    Page<AdZone> find(String name, AdZoneState state, Pageable pageable);
}

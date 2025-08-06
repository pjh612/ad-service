package com.example.adservice.domain.repository;

import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.model.search.BidSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BidForAdZoneRepository {
    BidForAdZone save(BidForAdZone bidForAdZone);

    Page<BidForAdZone> search(BidSearchCondition condition, Pageable pageable);

    Optional<BidForAdZone> findById(UUID id);
}

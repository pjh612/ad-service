package com.example.adadminservice.domain.repository;

import com.example.adadminservice.domain.model.BidNoticeForAdZone;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidNoticeForAdZoneRepository {
    BidNoticeForAdZone save(BidNoticeForAdZone bidAnnouncement);

    Optional<BidNoticeForAdZone> findById(UUID id);

    boolean existsActiveBidNoticeByAdZoneIdAndOverlappingPeriod(UUID id, Instant bidStartAt, Instant bidEndAt);

    List<BidNoticeForAdZone> findByZoneId(UUID zoneId);
}

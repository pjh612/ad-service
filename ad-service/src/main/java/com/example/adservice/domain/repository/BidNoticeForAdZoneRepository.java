package com.example.adservice.domain.repository;


import com.example.adservice.domain.model.BidNoticeForAdZone;
import com.example.adservice.domain.model.search.BidNoticeSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidNoticeForAdZoneRepository {
    BidNoticeForAdZone save(BidNoticeForAdZone bidAnnouncement);

    Optional<BidNoticeForAdZone> findById(UUID id);

    boolean existsActiveBidNoticeByAdZoneIdAndOverlappingPeriod(UUID id, Instant bidStartAt, Instant bidEndAt);

    List<BidNoticeForAdZone> findByZoneId(UUID zoneId);

    Page<BidNoticeForAdZone> search(BidNoticeSearchCondition condition, Pageable pageable);
}

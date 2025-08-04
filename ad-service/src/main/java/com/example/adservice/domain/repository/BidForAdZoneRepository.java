package com.example.adservice.domain.repository;

import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.model.BidStatus;

import java.util.List;
import java.util.UUID;

public interface BidForAdZoneRepository {
    BidForAdZone save(BidForAdZone bidForAdZone);

    BidForAdZone findById(UUID id);

    List<BidForAdZone> findByBidNoticeId(UUID bidNoticeId);

    boolean existsByBidNoticeIdAndBidStatus(UUID bidNoticeId, BidStatus bidStatus);
}

package com.example.adservice.domain.model.search;

import com.example.adservice.domain.model.BidNoticeState;

import java.time.Instant;
import java.util.UUID;

public record BidNoticeSearchCondition(
        String title,
        String authorName,
        BidNoticeState state,
        Instant adStartFrom,
        Instant adStartTo,
        Long minPrice,
        UUID zoneId
) {
    public BidNoticeSearchCondition {
        if (minPrice != null && minPrice < 0) {
            throw new IllegalArgumentException("최소 입찰가는 0 이상이어야 합니다.");
        }
    }
}

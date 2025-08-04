package com.example.adservice.application.in.dto;

import com.example.adservice.domain.model.BidNoticeState;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

@Builder
public record BidNoticeSearchRequest(
        String title,
        String authorName,
        BidNoticeState state,
        Instant adStartFrom,
        Instant adStartTo,
        Long minPrice,
        Long maxPrice,
        UUID zoneId,
        Pageable pageable
) {
}

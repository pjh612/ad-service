package com.example.adservice.application.in.dto;

import com.example.adservice.domain.model.BidNoticeForAdZone;
import com.example.adservice.domain.model.BidNoticeState;

import java.time.Instant;
import java.util.UUID;

public record BidNoticeForAdZoneResponse(
        UUID id,
        String title,
        String authorName,
        Instant bidStartAt,
        Instant bidEndAt,
        Instant adStartAt,
        Instant adEndAt,
        BidNoticeState state,
        Long minPrice,
        Long currentBidAmount) {

    public static BidNoticeForAdZoneResponse from(BidNoticeForAdZone entity) {
        return new BidNoticeForAdZoneResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthorName(),
                entity.getBidStartAt(),
                entity.getBidEndAt(),
                entity.getAdStartAt(),
                entity.getAdEndAt(),
                entity.getBidNoticeState(),
                entity.getMinPrice(),
                entity.getCurrentBidAmount()
        );
    }
}
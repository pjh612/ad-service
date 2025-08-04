package com.example.adservice.application.in.dto;

import com.example.adservice.domain.model.BidNoticeForAdZone;
import com.example.adservice.domain.model.BidNoticeState;

import java.time.Instant;
import java.util.UUID;

public record BidNoticeForAdZoneResponse(
        UUID id,
        String title,
        String authorName,
        Instant adStartAt,
        Instant adEndAt,
        BidNoticeState state,
        Long currentBidAmount) {

    public static BidNoticeForAdZoneResponse from(BidNoticeForAdZone entity) {
        return new BidNoticeForAdZoneResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthorName(),
                entity.getAdStartAt(),
                entity.getAdEndAt(),
                entity.getBidNoticeState(),
                entity.getCurrentBidAmount()
        );
    }
}
package com.example.adservice.application.in.dto;

import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.model.BidReviewStatus;
import com.example.adservice.domain.model.BidStatus;

import java.time.Instant;
import java.util.UUID;

public record BidForAdZoneResponse(
        UUID id,
        UUID bidNoticeId,
        UUID bidderId,
        String bidderName,
        Long amount,
        Instant bidAt,
        BidStatus bidStatus,
        String rejectReason,
        BidReviewStatus bidReviewStatus,
        String reviewRejectReason
) {

    public static BidForAdZoneResponse of(BidForAdZone bidForAdZone) {
        return new BidForAdZoneResponse(
                bidForAdZone.getId(),
                bidForAdZone.getBidNoticeId(),
                bidForAdZone.getBidderId(),
                bidForAdZone.getBidderName(),
                bidForAdZone.getAmount(),
                bidForAdZone.getBidAt(),
                bidForAdZone.getBidStatus(),
                bidForAdZone.getRejectReason(),
                bidForAdZone.getBidReviewStatus(),
                bidForAdZone.getReviewRejectReason()
        );
    }
}

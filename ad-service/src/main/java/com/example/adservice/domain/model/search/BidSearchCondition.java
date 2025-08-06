package com.example.adservice.domain.model.search;

import com.example.adservice.domain.model.BidReviewStatus;
import com.example.adservice.domain.model.BidStatus;

import java.util.UUID;

public record BidSearchCondition(
        UUID id,
        UUID bidNoticeId,
        UUID bidderId,
        String bidderName,
        BidStatus bidStatus,
        BidReviewStatus bidReviewStatus
) {
}

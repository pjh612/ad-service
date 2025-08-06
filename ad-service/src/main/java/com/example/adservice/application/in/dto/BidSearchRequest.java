package com.example.adservice.application.in.dto;

import com.example.adservice.domain.model.BidReviewStatus;
import com.example.adservice.domain.model.BidStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record BidSearchRequest(
        UUID id,
        UUID bidNoticeId,
        UUID bidderId,
        String bidderName,
        BidStatus bidStatus,
        BidReviewStatus bidReviewStatus,
        Pageable pageable
) {
}

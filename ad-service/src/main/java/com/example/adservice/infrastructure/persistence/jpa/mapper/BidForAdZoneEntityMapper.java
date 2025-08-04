package com.example.adservice.infrastructure.persistence.jpa.mapper;

import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.model.AuditInfo;
import com.example.adservice.infrastructure.persistence.jpa.entity.BidForAdZoneEntity;

public class BidForAdZoneEntityMapper {

    public static BidForAdZone toDomain(BidForAdZoneEntity entity) {
        return new BidForAdZone(
                entity.getId(),
                entity.getBidNoticeId(),
                entity.getBidderId(),
                entity.getBidderName(),
                entity.getAmount(),
                entity.getBidAt(),
                entity.getBidStatus(),
                entity.getRejectReason(),
                entity.getBidReviewStatus(),
                entity.getReviewRejectReason(),
                new AuditInfo(entity.getCreatedAt(), entity.getCreatedBy(), entity.getUpdatedAt(), entity.getUpdatedBy())
        );
    }

    public static BidForAdZoneEntity toEntity(BidForAdZone domain) {
        return BidForAdZoneEntity.builder()
                .id(domain.getId())
                .bidNoticeId(domain.getBidNoticeId())
                .bidderId(domain.getBidderId())
                .bidderName(domain.getBidderName())
                .amount(domain.getAmount())
                .bidAt(domain.getBidAt())
                .bidStatus(domain.getBidStatus())
                .rejectReason(domain.getRejectReason())
                .bidReviewStatus(domain.getBidReviewStatus())
                .reviewRejectReason(domain.getReviewRejectReason())
                .createdAt(domain.getAuditInfo().getCreatedAt())
                .createdBy(domain.getAuditInfo().getCreatedBy())
                .updatedAt(domain.getAuditInfo().getUpdatedAt())
                .updatedBy(domain.getAuditInfo().getUpdatedBy())
                .build();
    }
}

package com.example.adservice.infrastructure.persistence.jpa.entity;

import com.example.adservice.common.hibernate.annotations.UuidV7Generator;
import com.example.adservice.domain.model.BidNoticeState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bid_notice_for_ad_zones")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BidNoticeForAdZoneEntity extends BaseEntity {

    @Id
    @UuidV7Generator
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID authorId;

    @Column(nullable = false, length = 100)
    private String authorName;

    @Column(nullable = false)
    private Instant adStartAt;

    @Column(nullable = false)
    private Instant adEndAt;

    @Column(nullable = false)
    private Instant bidStartAt;

    @Column(nullable = false)
    private Instant bidEndAt;

    @Column(nullable = false)
    private Long minPrice;

    @Column
    private Long currentBidAmount;

    @Column(columnDefinition = "BINARY(16)")
    private UUID currentBidderId;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID zoneId;

    @Enumerated(EnumType.STRING)
    private BidNoticeState bidNoticeState;

    public BidNoticeForAdZoneEntity(UUID id, String title, String content, UUID authorId, String authorName, Instant adStartAt, Instant adEndAt, Instant bidStartAt, Instant bidEndAt, Long minPrice, Long currentBidAmount, UUID currentBidderId, UUID zoneId, BidNoticeState bidNoticeState, Instant createdAt, Instant updatedAt, String createdBy, String updatedBy) {
        super(createdAt, updatedAt, createdBy, updatedBy);
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.adStartAt = adStartAt;
        this.adEndAt = adEndAt;
        this.bidStartAt = bidStartAt;
        this.bidEndAt = bidEndAt;
        this.minPrice = minPrice;
        this.currentBidAmount = currentBidAmount;
        this.currentBidderId = currentBidderId;
        this.zoneId = zoneId;
        this.bidNoticeState = bidNoticeState;
    }
}

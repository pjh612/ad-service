package com.example.adservice.infrastructure.persistence.jpa.entity;

import com.example.adservice.common.hibernate.annotations.UuidV7Generator;
import com.example.adservice.domain.model.BidReviewStatus;
import com.example.adservice.domain.model.BidStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@Table(name = "bid_for_ad_zone")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BidForAdZoneEntity extends BaseEntity {

    @Id
    @UuidV7Generator
    private UUID id;

    @Column(nullable = false)
    private UUID bidNoticeId;

    @Column(nullable = false)
    private UUID bidderId;

    @Column(nullable = false)
    private String bidderName;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Instant bidAt;

    @Enumerated(EnumType.STRING)
    private BidStatus bidStatus;

    private String rejectReason;

    @Enumerated(EnumType.STRING)
    private BidReviewStatus bidReviewStatus;

    private String reviewRejectReason;
}

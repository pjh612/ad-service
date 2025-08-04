package com.example.adservice.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BidNoticeForAdZone {
    private UUID id;
    private String title;
    private String content;
    private UUID authorId;
    private String authorName;
    private Instant adStartAt;
    private Instant adEndAt;
    private Instant bidStartAt;
    private Instant bidEndAt;
    private Long minPrice;
    private Long currentBidAmount;
    private UUID currentBidderId;
    private UUID zoneId;
    private BidNoticeState bidNoticeState;
    private AuditInfo auditInfo;

    private BidNoticeForAdZone(
            String title,
            String content,
            UUID authorId,
            String authorName,
            Instant adStartAt,
            Instant adEndAt,
            Instant bidStartAt,
            Instant bidEndAt,
            Long minPrice,
            UUID zoneId,
            BidNoticeState bidNoticeState,
            AuditInfo auditInfo
    ) {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("제목은 필수 입력값입니다.");
        }

        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("내용은 필수 입력값입니다.");
        }

        if (authorId == null || !StringUtils.hasText(authorName)) {
            throw new IllegalArgumentException("작성자 정보가 올바르지 않습니다.");
        }

        if (adStartAt == null || adEndAt == null || adEndAt.isBefore(adStartAt)) {
            throw new IllegalArgumentException("광고 종료일은 광고 시작일 이후여야 합니다.");
        }

        if (bidStartAt == null || bidEndAt == null || bidEndAt.isBefore(bidStartAt)) {
            throw new IllegalArgumentException("입찰 종료일은 입찰 시작일 이후여야 합니다.");
        }

        if (adStartAt.isBefore(bidEndAt)) {
            throw new IllegalArgumentException("입찰 마감일은 광고 시작일 이전이어야 합니다.");
        }

        if (minPrice == null || minPrice < 0) {
            throw new IllegalArgumentException("최소 입찰가는 0원 이상이어야 합니다.");
        }

        if (zoneId == null) {
            throw new IllegalArgumentException("광고 존 ID는 필수값입니다.");
        }


        this.bidStartAt = bidStartAt;
        this.bidEndAt = bidEndAt;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.adStartAt = adStartAt;
        this.adEndAt = adEndAt;
        this.minPrice = minPrice;
        this.zoneId = zoneId;
        this.bidNoticeState = bidNoticeState;
        this.auditInfo = auditInfo;
    }

    public static BidNoticeForAdZone create(
            String title,
            String content,
            UUID authorId,
            String authorName,
            Instant adStartAt,
            Instant adEndAt,
            Instant bidStartAt,
            Instant bidEndAt,
            Long minPrice,
            UUID zoneId
    ) {
        return new BidNoticeForAdZone(title, content, authorId, authorName, adStartAt, adEndAt, bidStartAt, bidEndAt, minPrice, zoneId, BidNoticeState.READY, AuditInfo.create(authorName));
    }

    public void bid(Long bidAmount, UUID bidderId) {
        if (bidAmount <= 0) {
            throw new IllegalArgumentException("입찰 금액은 0원보다 커야 합니다.");
        }

        if (bidAmount < this.minPrice) {
            throw new IllegalArgumentException(
                    String.format("입찰 금액은 최소 금액(%d원) 이상이어야 합니다.", this.minPrice)
            );
        }

        if (this.currentBidAmount != null && bidAmount <= this.currentBidAmount) {
            throw new IllegalArgumentException(
                    String.format("입찰 금액은 현재 최고 입찰가(%d원)보다 커야 합니다.", this.currentBidAmount)
            );
        }

        this.currentBidAmount = bidAmount;
        this.currentBidderId = bidderId;
        this.auditInfo.update(bidderId.toString());
    }

    public boolean isBiddableAt(Instant bidTime) {
        return !bidTime.isBefore(bidStartAt) && !bidTime.isAfter(bidEndAt);
    }
}

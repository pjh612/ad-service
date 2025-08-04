package com.example.adservice.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BidForAdZone {
    private UUID id;
    private UUID bidNoticeId;
    private UUID bidderId;
    private String bidderName;
    private Long amount;
    private Instant bidAt;
    private BidStatus bidStatus;
    private String rejectReason;
    private BidReviewStatus bidReviewStatus;
    private String reviewRejectReason;
    private AuditInfo auditInfo;

    public BidForAdZone(UUID id, UUID bidNoticeId, UUID bidderId, String bidderName, Long amount, Instant bidAt, BidStatus bidStatus, String rejectReason, BidReviewStatus bidReviewStatus, String reviewRejectReason, AuditInfo auditInfo) {
        this.id = id;
        this.bidNoticeId = bidNoticeId;
        this.bidderId = bidderId;
        this.bidderName = bidderName;
        this.amount = amount;
        this.bidAt = bidAt;
        this.bidStatus = bidStatus;
        this.rejectReason = rejectReason;
        this.bidReviewStatus = bidReviewStatus;
        this.reviewRejectReason = reviewRejectReason;
        this.auditInfo = auditInfo;
    }

    public BidForAdZone(UUID bidNoticeId, UUID bidderId, String bidderName, Long amount, Instant bidAt, AuditInfo auditInfo) {
        this(null, bidNoticeId, bidderId, bidderName, amount, bidAt, BidStatus.SUBMITTED, null, BidReviewStatus.PENDING, null, auditInfo);
    }

    public static BidForAdZone create(UUID bidNoticeId, UUID bidderId, String bidderName, Long amount, Instant bidAt) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        return new BidForAdZone(bidNoticeId, bidderId, bidderName, amount, bidAt, AuditInfo.create(bidderId.toString()));
    }

    public void cancelBid() {
        if (bidStatus != BidStatus.SUBMITTED) {
            throw new IllegalStateException("입찰 취소할 수 있는 상태가 아닙니다.");
        }
        this.bidStatus = BidStatus.CANCELED;
    }

    public void award() {
        if (bidStatus != BidStatus.SUBMITTED) {
            throw new IllegalStateException("낙찰 처리할 수 없는 입찰 상태입니다.");
        }
        if (bidReviewStatus != BidReviewStatus.PASSED) {
            throw new IllegalStateException("심사를 통과한 입찰만 낙찰 처리할 수 있습니다.");
        }

        this.bidStatus = BidStatus.AWARDED;
    }

    public void rejectBid(String rejectReason) {
        if (this.bidStatus != BidStatus.SUBMITTED) {
            throw new IllegalStateException("거절할 수 없는 상태입니다.");
        }

        if (this.bidReviewStatus != BidReviewStatus.REJECTED) {
            throw new IllegalStateException("반려된 입찰만 최종 거절할 수 있습니다.");
        }

        this.bidStatus = BidStatus.REJECTED;
        this.rejectReason = rejectReason;
    }


    public void startReview() {
        if (this.bidStatus != BidStatus.SUBMITTED) {
            throw new IllegalStateException("제출된 입찰만 심사할 수 있습니다.");
        }

        if (bidReviewStatus != BidReviewStatus.PENDING) {
            throw new IllegalStateException("리뷰 대기 상태가 아닙니다.");
        }
        this.bidReviewStatus = BidReviewStatus.REVIEWING;
    }

    public void rejectReview(String rejectReason) {
        if (bidReviewStatus != BidReviewStatus.REVIEWING) {
            throw new IllegalStateException("리뷰 중 상태가 아닙니다.");
        }
        this.bidReviewStatus = BidReviewStatus.REJECTED;
        this.reviewRejectReason = rejectReason;
    }

    public void passReview() {
        if (bidReviewStatus != BidReviewStatus.REVIEWING) {
            throw new IllegalStateException("리뷰 중 상태가 아닙니다.");
        }
        this.bidReviewStatus = BidReviewStatus.PASSED;
    }


}

package com.example.adservice.domain;

import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.model.BidReviewStatus;
import com.example.adservice.domain.model.BidStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class BidForAdZoneTest {

    private UUID bidNoticeId;
    private UUID bidderId;
    private Instant now;

    @BeforeEach
    void setUp() {
        bidNoticeId = UUID.randomUUID();
        bidderId = UUID.randomUUID();
        now = Instant.now();
    }

    @Test
    void 생성_성공() {
        BidForAdZone bid = BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 10000L, now);

        assertThat(bid.getBidStatus()).isEqualTo(BidStatus.SUBMITTED);
        assertThat(bid.getBidReviewStatus()).isEqualTo(BidReviewStatus.PENDING);
    }

    @Test
    void 입찰금액이_0이하면_예외() {
        assertThatThrownBy(() ->
                BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 0L, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be greater than 0");
    }

    @Test
    void 입찰_취소_성공() {
        BidForAdZone bid = BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 10000L, now);
        bid.cancelBid();

        assertThat(bid.getBidStatus()).isEqualTo(BidStatus.CANCELED);
    }

    @Test
    void 입찰_취소_불가_상태에서_예외() {
        BidForAdZone bid = BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 10000L, now);
        bid.cancelBid();

        assertThatThrownBy(bid::cancelBid)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("입찰 취소할 수 있는 상태가 아닙니다.");
    }

    @Test
    void 심사_시작_성공() {
        BidForAdZone bid = BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 10000L, now);
        bid.startReview();

        assertThat(bid.getBidReviewStatus()).isEqualTo(BidReviewStatus.REVIEWING);
    }

    @Test
    void 심사_반려_성공() {
        BidForAdZone bid = BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 10000L, now);
        bid.startReview();
        bid.rejectReview("내용이 부적절합니다");

        assertThat(bid.getBidReviewStatus()).isEqualTo(BidReviewStatus.REJECTED);
        assertThat(bid.getReviewRejectReason()).isEqualTo("내용이 부적절합니다");
    }

    @Test
    void 심사_통과_성공() {
        BidForAdZone bid = BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 10000L, now);
        bid.startReview();
        bid.passReview();

        assertThat(bid.getBidReviewStatus()).isEqualTo(BidReviewStatus.PASSED);
    }

    @Test
    void 낙찰_성공() {
        BidForAdZone bid = BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 10000L, now);
        bid.startReview();
        bid.passReview();
        bid.award();

        assertThat(bid.getBidStatus()).isEqualTo(BidStatus.AWARDED);
    }

    @Test
    void 리뷰_반려후_최종_거절() {
        BidForAdZone bid = BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 10000L, now);
        bid.startReview();
        bid.rejectReview("이미지 부적절");
        bid.rejectBid("최종 심사 거절");

        assertThat(bid.getBidStatus()).isEqualTo(BidStatus.REJECTED);
        assertThat(bid.getRejectReason()).isEqualTo("최종 심사 거절");
    }

    @Test
    void 반려되지_않은_입찰은_거절_불가() {
        BidForAdZone bid = BidForAdZone.create(bidNoticeId, bidderId, "광고주1", 10000L, now);
        bid.startReview();
        bid.passReview();

        assertThatThrownBy(() -> bid.rejectBid("최종 거절"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("반려된 입찰만 최종 거절할 수 있습니다.");
    }
}

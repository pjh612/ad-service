package com.example.adservice.application.service;

import com.example.adservice.TestClockConfiguration;
import com.example.adservice.TestRedisConfiguration;
import com.example.adservice.application.in.dto.BidAdvertisementRequest;
import com.example.adservice.domain.model.AdZone;
import com.example.adservice.domain.model.AdZoneState;
import com.example.adservice.domain.model.Advertiser;
import com.example.adservice.domain.model.AuditInfo;
import com.example.adservice.domain.model.BidNoticeForAdZone;
import com.example.adservice.domain.repository.AdZoneRepository;
import com.example.adservice.domain.repository.AdvertiserRepository;
import com.example.adservice.domain.repository.BidNoticeForAdZoneRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@Import({TestRedisConfiguration.class, TestClockConfiguration.class})
@ActiveProfiles("test")
public class BidAdvertisementIntegrationTest {
    @Autowired
    private BidAdvertisementService bidAdvertisementService;

    @Autowired
    private AdZoneRepository adZoneRepository;

    @Autowired
    private BidNoticeForAdZoneRepository bidNoticeForAdZoneRepository;

    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private Clock clock;

    @Test
    void 동시_입찰_요청_테스트() throws InterruptedException {
        int threadCount = 32;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        UUID adZoneId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID advertiserId = UUID.randomUUID();

        Instant adStartAt = LocalDateTime.now(clock).plusDays(10).atZone(ZoneId.systemDefault()).toInstant();
        Instant adEndAt = LocalDateTime.now(clock).plusDays(11).atZone(ZoneId.systemDefault()).toInstant();

        Instant bidStartAt = LocalDateTime.now(clock).minusDays(2).atZone(ZoneId.systemDefault()).toInstant();
        Instant bidEndAt = LocalDateTime.now(clock).plusDays(2).atZone(ZoneId.systemDefault()).toInstant();

        AdZone adZone = new AdZone(adZoneId, "메인배너구좌", "서비스 메인 배너 광고 구좌", 500, 500, 1000, AdZoneState.EMPTY, AuditInfo.create(authorId.toString()));
        BidNoticeForAdZone bidNotice = BidNoticeForAdZone.create(
                "공고제목", "내용", authorId, "관리자", adStartAt, adEndAt, bidStartAt, bidEndAt, 10000L, adZoneId
        );
        BidNoticeForAdZone savedBidNotice = bidNoticeForAdZoneRepository.save(bidNotice);
        Advertiser advertiser = new Advertiser(advertiserId, "test01", "test", "광고주1", "test@gmail.com", AuditInfo.create(advertiserId.toString()));

        adZoneRepository.save(adZone);
        advertiserRepository.save(advertiser);

        // 동시 입찰 시도
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    long bidAmount = 10000L + index * 1000;
                    BidAdvertisementRequest request = new BidAdvertisementRequest(savedBidNotice.getId(), adZoneId, advertiser.getId(), bidAmount);
                    bidAdvertisementService.bid(request);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 최고 입찰 확인
        BidNoticeForAdZone finalNotice = bidNoticeForAdZoneRepository.findById(savedBidNotice.getId()).orElseThrow();

        System.out.println(finalNotice.getCurrentBidAmount());
        Assertions.assertThat(finalNotice.getCurrentBidAmount()).isEqualTo(10000L + (threadCount - 1) * 1000); // 가장 큰 금액
    }
}

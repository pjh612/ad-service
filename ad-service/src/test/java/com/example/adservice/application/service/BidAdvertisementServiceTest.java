package com.example.adservice.application.service;

import com.example.adservice.application.in.dto.BidAdvertisementRequest;
import com.example.adservice.application.in.dto.BidForAdZoneResponse;
import com.example.adservice.domain.model.*;
import com.example.adservice.domain.repository.AdZoneRepository;
import com.example.adservice.domain.repository.AdvertiserRepository;
import com.example.adservice.domain.repository.BidForAdZoneRepository;
import com.example.adservice.domain.repository.BidNoticeForAdZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BidAdvertisementServiceTest {

    @Mock
    private AdZoneRepository adZoneRepository;

    @Mock
    private BidNoticeForAdZoneRepository bidNoticeForAdZoneRepository;

    @Mock
    private AdvertiserRepository advertiserRepository;

    @Mock
    private BidForAdZoneRepository bidForAdZoneRepository;

    @InjectMocks
    private BidAdvertisementService bidAdvertisementService;

    private Clock fixedClock;

    @BeforeEach
    void setup() {
        fixedClock = Clock.fixed(LocalDateTime.of(2025,7,6,14,0,0).atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        this.bidAdvertisementService = new BidAdvertisementService(fixedClock, adZoneRepository, bidNoticeForAdZoneRepository, advertiserRepository, bidForAdZoneRepository);
    }

    @Test
    void bid_shouldSucceed_whenAllValidationsPass() {
        // Given
        UUID adZoneId = UUID.randomUUID();
        UUID bidNoticeId = UUID.randomUUID();
        UUID advertiserId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        Long bidAmount = 100_000L;

        Instant adStartAt = LocalDateTime.of(2025, 7, 7, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
        Instant adEndAt = LocalDateTime.of(2025, 7, 7, 23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        Instant bidStartAt = LocalDateTime.of(2025, 7, 6, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
        Instant bidEndAt = LocalDateTime.of(2025, 7, 6, 23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        BidAdvertisementRequest request = new BidAdvertisementRequest(bidNoticeId, adZoneId, advertiserId, bidAmount);

        AdZone adZone = new AdZone(adZoneId, "메인배너구좌", "서비스 메인 배너 광고 구좌", 500, 500, 1000, AdZoneState.EMPTY, AuditInfo.create(authorId.toString()));
        given(adZoneRepository.findById(adZoneId)).willReturn(Optional.of(adZone));
        BidNoticeForAdZone bidNotice = new BidNoticeForAdZone(bidNoticeId, "title", "content", authorId, "관리자", adStartAt, adEndAt, bidStartAt, bidEndAt, 1000L, 1000L, null, adZoneId, BidNoticeState.READY, AuditInfo.create("관리자"));
        given(bidNoticeForAdZoneRepository.findById(bidNoticeId)).willReturn(Optional.of(bidNotice));
        given(bidNoticeForAdZoneRepository.save(any())).willReturn(bidNotice);

        Advertiser advertiser = new Advertiser(advertiserId, "test", "test", "테스트광고주", "test@gmail.com", AuditInfo.create(advertiserId.toString()));
        given(advertiserRepository.findById(advertiserId)).willReturn(Optional.of(advertiser));

        BidForAdZone bid = BidForAdZone.create(bidNoticeId, advertiserId, "테스트광고주", bidAmount, Instant.now());
        given(bidForAdZoneRepository.save(any())).willReturn(bid);

        // When
        BidForAdZoneResponse response = bidAdvertisementService.bid(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.amount()).isEqualTo(bidAmount);
        assertThat(response.bidderId()).isEqualTo(advertiserId);
    }
}

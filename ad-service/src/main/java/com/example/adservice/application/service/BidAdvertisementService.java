package com.example.adservice.application.service;

import com.example.adservice.application.in.BidAdvertisementUseCase;
import com.example.adservice.application.in.dto.BidAdvertisementRequest;
import com.example.adservice.application.in.dto.BidForAdZoneResponse;
import com.example.adservice.common.lock.RedisLock;
import com.example.adservice.domain.model.AdZone;
import com.example.adservice.domain.model.Advertiser;
import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.model.BidNoticeForAdZone;
import com.example.adservice.domain.repository.AdZoneRepository;
import com.example.adservice.domain.repository.AdvertiserRepository;
import com.example.adservice.domain.repository.BidForAdZoneRepository;
import com.example.adservice.domain.repository.BidNoticeForAdZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BidAdvertisementService implements BidAdvertisementUseCase {
    private final Clock clock;
    private final AdZoneRepository adZoneRepository;
    private final BidNoticeForAdZoneRepository bidNoticeForAdZoneRepository;
    private final AdvertiserRepository advertiserRepository;
    private final BidForAdZoneRepository bidForAdZoneRepository;

    @Override
    @Transactional
    @RedisLock(waitTime = 5, leaseTime = 1, key = "#request.bidNoticeForAdZoneId")
    public BidForAdZoneResponse bid(BidAdvertisementRequest request) {
        Instant now = Instant.now(clock);

        //BidNoticeForAdZone(구좌 입찰 공고) 유효성 확인
        BidNoticeForAdZone bidNoticeForAdZone = bidNoticeForAdZoneRepository.findById(request.bidNoticeForAdZoneId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("입찰 공고가 존재하지 않습니다. id: %s", request.bidNoticeForAdZoneId())));

        if (!bidNoticeForAdZone.isBiddableAt(now)) {
            throw new IllegalArgumentException("Bid notice for ad zone is not biddable.");
        }

        // adZone조회, 유효한지?
        adZoneRepository.findById(bidNoticeForAdZone.getZoneId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("광고 구좌가 존재하지 않습니다. %s", bidNoticeForAdZone.getZoneId())));

        //입찰 광고주 유효성 확인
        Advertiser advertiser = advertiserRepository.findById(request.advertiserId())
                .orElseThrow(() -> new IllegalArgumentException("Advertiser not found"));


        //입찰 최고금액이 현재 입찰 요청 금액보다 낮아야함 (동시성 보장 필요)
        bidNoticeForAdZone.bid(request.amount(), advertiser.getId());
        bidNoticeForAdZoneRepository.save(bidNoticeForAdZone);

        //입찰 요청, PENDING 상태
        BidForAdZone newBid = BidForAdZone.create(bidNoticeForAdZone.getId(), advertiser.getId(), advertiser.getName(), request.amount(), now);
        BidForAdZone savedBid = bidForAdZoneRepository.save(newBid);


        return BidForAdZoneResponse.of(savedBid);
    }
}

package com.example.adadminservice.application.service;

import com.example.adadminservice.application.event.BidNoticeRegisteredEvent;
import com.example.adadminservice.application.in.RegisterBidNoticeForAdZoneUseCase;
import com.example.adadminservice.application.in.dto.RegisterBidNoticeForAdZoneRequest;
import com.example.adadminservice.application.in.dto.RegisterBidNoticeForAdZoneResponse;
import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.model.Admin;
import com.example.adadminservice.domain.model.BidNoticeForAdZone;
import com.example.adadminservice.domain.repository.AdZoneRepository;
import com.example.adadminservice.domain.repository.AdminRepository;
import com.example.adadminservice.domain.repository.BidNoticeForAdZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RegisterBidNoticeForAdZoneService implements RegisterBidNoticeForAdZoneUseCase {
    private final BidNoticeForAdZoneRepository bidNoticeForAdZoneRepository;
    private final AdZoneRepository adZoneRepository;
    private final AdminRepository adminRepository;
    private final Clock clock;

    @Override
    @Transactional
    public RegisterBidNoticeForAdZoneResponse register(RegisterBidNoticeForAdZoneRequest request) {
        //광고 구좌 존재하는지 확인
        AdZone adZone = adZoneRepository.findById(request.zoneId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 광고 구좌 입니다."));

        //광고 시작일이 오늘 이후인지
        if (request.bidStartAt().isBefore(Instant.now(clock))) {
            throw new IllegalArgumentException("광고 시작일은 현재 시간 이후여야 합니다.");
        }

        //올라온 공고 중 광고 기간이 겹치는지
        boolean alreadyExists = bidNoticeForAdZoneRepository.existsActiveBidNoticeByAdZoneIdAndOverlappingPeriod(adZone.getId(), request.adStartAt(), request.adEndAt());
        if (alreadyExists) {
            throw new IllegalArgumentException("해당 구좌는 지정한 기간에 이미 광고 예정이거나 광고 중입니다.");
        }

        //작성자 유효성 확인
        Admin admin = adminRepository.findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를  찾을 수 없습니다."));

        //게시글 생성
        BidNoticeForAdZone bidNoticeForAdZone = BidNoticeForAdZone.create(
                request.title(),
                request.content(),
                admin.getId(),
                admin.getLastName() + admin.getFirstName(),
                request.adStartAt(),
                request.adEndAt(),
                request.bidStartAt(),
                request.bidEndAt(),
                request.minPrice(),
                request.zoneId()
        );

        //저장
        BidNoticeForAdZone savedNotice = bidNoticeForAdZoneRepository.save(bidNoticeForAdZone);

        return new RegisterBidNoticeForAdZoneResponse(savedNotice.getId());
    }
}

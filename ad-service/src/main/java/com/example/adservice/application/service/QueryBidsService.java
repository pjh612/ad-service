package com.example.adservice.application.service;

import com.example.adservice.application.in.QueryBidsUseCase;
import com.example.adservice.application.in.dto.BidForAdZoneResponse;
import com.example.adservice.application.in.dto.BidSearchRequest;
import com.example.adservice.domain.model.search.BidSearchCondition;
import com.example.adservice.domain.repository.BidForAdZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryBidsService implements QueryBidsUseCase {
    private final BidForAdZoneRepository bidForAdZoneRepository;

    @Override
    public Page<BidForAdZoneResponse> search(BidSearchRequest request) {
        BidSearchCondition condition = toCondition(request);
        return bidForAdZoneRepository.search(condition, request.pageable())
                .map(BidForAdZoneResponse::of);
    }

    @Override
    public BidForAdZoneResponse findById(UUID id) {
        return bidForAdZoneRepository.findById(id)
                .map(BidForAdZoneResponse::of)
                .orElseThrow(() -> new RuntimeException("입찰을 찾을 수 없습니다."));
    }

    private BidSearchCondition toCondition(BidSearchRequest request) {
        return new BidSearchCondition(
                request.id(),
                request.bidNoticeId(),
                request.bidderId(),
                request.bidderName(),
                request.bidStatus(),
                request.bidReviewStatus()
        );
    }
}

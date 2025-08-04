package com.example.adservice.application.service;

import com.example.adservice.application.in.QueryBidNoticeForAdZoneUseCase;
import com.example.adservice.application.in.dto.BidNoticeForAdZoneResponse;
import com.example.adservice.application.in.dto.BidNoticeSearchRequest;
import com.example.adservice.domain.model.search.BidNoticeSearchCondition;
import com.example.adservice.domain.repository.BidNoticeForAdZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryBidNoticeForAdZoneService implements QueryBidNoticeForAdZoneUseCase {
    private final BidNoticeForAdZoneRepository bidNoticeForAdZoneRepository;

    @Override
    public Page<BidNoticeForAdZoneResponse> search(BidNoticeSearchRequest request) {
        BidNoticeSearchCondition condition = toCondition(request);
        return bidNoticeForAdZoneRepository.search(condition, request.pageable())
                .map(BidNoticeForAdZoneResponse::from);
    }

    private BidNoticeSearchCondition toCondition(BidNoticeSearchRequest request) {
        return new BidNoticeSearchCondition(
                request.title(),
                request.authorName(),
                request.state(),
                request.adStartFrom(),
                request.adStartTo(),
                request.minPrice(),
                request.zoneId()
        );
    }
}

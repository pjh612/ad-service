package com.example.adservice.application.in;

import com.example.adservice.application.in.dto.BidNoticeForAdZoneResponse;
import com.example.adservice.application.in.dto.BidNoticeSearchRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface QueryBidNoticeForAdZoneUseCase {

    Page<BidNoticeForAdZoneResponse> search(BidNoticeSearchRequest request);

    BidNoticeForAdZoneResponse findById(UUID id);
}

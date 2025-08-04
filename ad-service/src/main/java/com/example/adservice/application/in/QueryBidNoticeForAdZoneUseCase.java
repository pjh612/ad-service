package com.example.adservice.application.in;

import com.example.adservice.application.in.dto.BidNoticeForAdZoneResponse;
import com.example.adservice.application.in.dto.BidNoticeSearchRequest;
import org.springframework.data.domain.Page;

public interface QueryBidNoticeForAdZoneUseCase {

    Page<BidNoticeForAdZoneResponse> search(BidNoticeSearchRequest request);
}

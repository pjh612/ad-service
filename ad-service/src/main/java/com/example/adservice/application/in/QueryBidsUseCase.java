package com.example.adservice.application.in;

import com.example.adservice.application.in.dto.BidForAdZoneResponse;
import com.example.adservice.application.in.dto.BidSearchRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface QueryBidsUseCase {
    Page<BidForAdZoneResponse> search(BidSearchRequest request);

    BidForAdZoneResponse findById(UUID id);
}

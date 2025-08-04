package com.example.adservice.application.in;

import com.example.adservice.application.in.dto.BidAdvertisementRequest;
import com.example.adservice.application.in.dto.BidForAdZoneResponse;

public interface BidAdvertisementUseCase {
    BidForAdZoneResponse bid(BidAdvertisementRequest request);
}

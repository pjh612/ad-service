package com.example.adservice.infrastructure.web.controller;

import com.example.adservice.application.in.dto.BidAdvertisementRequest;
import com.example.adservice.application.in.dto.BidForAdZoneResponse;
import com.example.adservice.application.in.BidAdvertisementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidAdvertisementUseCase bidAdvertisementUseCase;

    @PostMapping
    public BidForAdZoneResponse bid(@RequestBody BidAdvertisementRequest request) {
        return bidAdvertisementUseCase.bid(request);
    }
}

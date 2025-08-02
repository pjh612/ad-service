package com.example.adadminservice.infrastructure.web.controller;

import com.example.adadminservice.application.in.RegisterBidNoticeForAdZoneUseCase;
import com.example.adadminservice.application.in.dto.RegisterBidNoticeForAdZoneRequest;
import com.example.adadminservice.application.in.dto.RegisterBidNoticeForAdZoneResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bid-notices")
public class BidNoticeForAdZoneController {
    private final RegisterBidNoticeForAdZoneUseCase registerBidNoticeForAdZoneUseCase;

    @PostMapping
    public RegisterBidNoticeForAdZoneResponse register(@RequestBody RegisterBidNoticeForAdZoneRequest request) {
        return registerBidNoticeForAdZoneUseCase.register(request);
    }
}

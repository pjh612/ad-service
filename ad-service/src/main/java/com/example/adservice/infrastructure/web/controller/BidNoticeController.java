package com.example.adservice.infrastructure.web.controller;

import com.example.adservice.application.in.QueryBidNoticeForAdZoneUseCase;
import com.example.adservice.application.in.dto.BidNoticeForAdZoneResponse;
import com.example.adservice.application.in.dto.BidNoticeSearchRequest;
import com.example.adservice.domain.model.BidNoticeState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bid-notices")
@RequiredArgsConstructor
public class BidNoticeController {
    private final QueryBidNoticeForAdZoneUseCase queryBidNoticeForAdZoneUseCase;

    @GetMapping
    public ResponseEntity<Page<BidNoticeForAdZoneResponse>> getAllBidNotices(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) BidNoticeState state,
            @PageableDefault Pageable pageable) {

        BidNoticeSearchRequest request = new BidNoticeSearchRequest(
                title, null, state, null, null, null, null, null, pageable
        );
        return ResponseEntity.ok(queryBidNoticeForAdZoneUseCase.search(request));
    }
}

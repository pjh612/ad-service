package com.example.adservice.infrastructure.web.controller;

import com.example.adservice.application.in.QueryBidNoticeForAdZoneUseCase;
import com.example.adservice.application.in.dto.BidNoticeForAdZoneResponse;
import com.example.adservice.application.in.dto.BidNoticeSearchRequest;
import com.example.adservice.domain.model.BidNoticeState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bid-notices")
@RequiredArgsConstructor
public class BidNoticeController {
    private final QueryBidNoticeForAdZoneUseCase queryBidNoticeForAdZoneUseCase;

    @GetMapping
    public PagedModel<BidNoticeForAdZoneResponse> getAllBidNotices(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) BidNoticeState state,
            @PageableDefault Pageable pageable) {

        BidNoticeSearchRequest request = new BidNoticeSearchRequest(
                title, null, state, null, null, null, null, null, pageable
        );
        return new PagedModel<>(queryBidNoticeForAdZoneUseCase.search(request));
    }

    @GetMapping("/{id}")
    public BidNoticeForAdZoneResponse getById(@PathVariable UUID id) {
        return queryBidNoticeForAdZoneUseCase.findById(id);
    }
}

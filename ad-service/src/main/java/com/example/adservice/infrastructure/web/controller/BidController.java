package com.example.adservice.infrastructure.web.controller;

import com.example.adservice.application.in.BidAdvertisementUseCase;
import com.example.adservice.application.in.QueryBidsUseCase;
import com.example.adservice.application.in.dto.BidAdvertisementRequest;
import com.example.adservice.application.in.dto.BidForAdZoneResponse;
import com.example.adservice.application.in.dto.BidSearchRequest;
import com.example.adservice.domain.model.BidReviewStatus;
import com.example.adservice.domain.model.BidStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidAdvertisementUseCase bidAdvertisementUseCase;
    private final QueryBidsUseCase queryBidsUseCase;

    @PostMapping
    public BidForAdZoneResponse bid(@RequestBody BidAdvertisementRequest request) {
        return bidAdvertisementUseCase.bid(request);
    }

    @GetMapping
    public PagedModel<BidForAdZoneResponse> getAllBidNotices(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID bidNoticeId,
            @RequestParam(required = false) UUID bidderId,
            @RequestParam(required = false) String bidderName,
            @RequestParam(required = false) BidStatus bidStatus,
            @RequestParam(required = false) BidReviewStatus bidReviewStatus,
            @PageableDefault Pageable pageable) {

        BidSearchRequest request = new BidSearchRequest(
                id, bidNoticeId, bidderId, bidderName, bidStatus, bidReviewStatus, pageable
        );
        return new PagedModel<>(queryBidsUseCase.search(request));
    }

    @GetMapping("/my-bids")
    public PagedModel<BidForAdZoneResponse> getMyBids(@RequestParam(required = false) UUID bidNoticeId,
                                                      @RequestParam(required = false) BidStatus bidStatus,
                                                      @RequestParam(required = false) BidReviewStatus bidReviewStatus,
                                                      @AuthenticationPrincipal UserDetails userDetails,
                                                      @PageableDefault Pageable pageable) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        BidSearchRequest request = new BidSearchRequest(
                null, bidNoticeId, userId, null, bidStatus, bidReviewStatus, pageable
        );

        return new PagedModel<>(queryBidsUseCase.search(request));
    }


}

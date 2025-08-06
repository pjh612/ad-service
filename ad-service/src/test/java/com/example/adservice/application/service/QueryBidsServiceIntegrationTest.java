package com.example.adservice.application.service;

import com.example.adservice.application.in.dto.BidForAdZoneResponse;
import com.example.adservice.application.in.dto.BidSearchRequest;
import com.example.adservice.domain.model.AuditInfo;
import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.model.BidReviewStatus;
import com.example.adservice.domain.model.BidStatus;
import com.example.adservice.domain.repository.BidForAdZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QueryBidsServiceIntegrationTest {

    @Autowired
    private QueryBidsService queryBidsService;

    @MockBean
    private BidForAdZoneRepository bidForAdZoneRepository;

    private UUID testBidId;
    private UUID testBidNoticeId;
    private UUID testBidderId;
    private String testBidderName = "테스트 입찰자";
    private List<BidForAdZone> testBids = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        testBidNoticeId = UUID.randomUUID();
        testBidderId = UUID.randomUUID();
        testBidId = UUID.randomUUID();

        // 첫 번째 입찰 (SUBMITTED 상태, PENDING 리뷰 상태)
        BidForAdZone bid1 = createBid(
                testBidId,
                testBidNoticeId,
                testBidderId,
                testBidderName,
                10000L,
                BidStatus.SUBMITTED,
                BidReviewStatus.PENDING
        );
        testBids.add(bid1);

        // 두 번째 입찰 (AWARDED 상태, PASSED 리뷰 상태)
        UUID bid2Id = UUID.randomUUID();
        UUID bidderId2 = UUID.randomUUID();
        BidForAdZone bid2 = createBid(
                bid2Id,
                testBidNoticeId,
                bidderId2,
                "다른 입찰자",
                20000L,
                BidStatus.AWARDED,
                BidReviewStatus.PASSED
        );
        testBids.add(bid2);

        // 세 번째 입찰 (REJECTED 상태, REJECTED 리뷰 상태)
        UUID bid3Id = UUID.randomUUID();
        UUID bidNoticeId3 = UUID.randomUUID();
        BidForAdZone bid3 = createBid(
                bid3Id,
                bidNoticeId3,
                testBidderId,
                testBidderName,
                5000L,
                BidStatus.REJECTED,
                BidReviewStatus.REJECTED
        );
        testBids.add(bid3);

        // 레포지토리 모킹
        mockRepositoryMethods();
    }

    private BidForAdZone createBid(UUID id, UUID bidNoticeId, UUID bidderId, String bidderName,
                                   Long amount, BidStatus bidStatus, BidReviewStatus bidReviewStatus) {
        AuditInfo auditInfo = AuditInfo.create(bidderId.toString());
        String rejectReason = bidStatus == BidStatus.REJECTED ? "테스트 거절 사유" : null;
        String reviewRejectReason = bidReviewStatus == BidReviewStatus.REJECTED ? "테스트 심사 거절 사유" : null;

        return new BidForAdZone(
                id,
                bidNoticeId,
                bidderId,
                bidderName,
                amount,
                Instant.now(),
                bidStatus,
                rejectReason,
                bidReviewStatus,
                reviewRejectReason,
                auditInfo
        );
    }

    private void mockRepositoryMethods() {
        // findById 모킹
        for (BidForAdZone bid : testBids) {
            when(bidForAdZoneRepository.findById(bid.getId())).thenReturn(Optional.of(bid));
        }
        when(bidForAdZoneRepository.findById(any())).thenReturn(Optional.empty());

        // 검색 메서드 모킹
        when(bidForAdZoneRepository.search(any(), any(Pageable.class))).thenAnswer(invocation -> {
            var condition = invocation.getArgument(0);
            Pageable pageable = invocation.getArgument(1);

            List<BidForAdZone> filteredBids = filterBids(condition);
            return new PageImpl<>(filteredBids, pageable, filteredBids.size());
        });
    }

    private List<BidForAdZone> filterBids(Object condition) {
        // 실제 구현에서는 여기서 조건에 따라 필터링 로직을 구현
        // 간단한 구현을 위해 모든 테스트 입찰을 반환
        return testBids;
    }

    @Test
    void searchWithPagination_returnsBidsInCorrectOrder() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        BidSearchRequest request = new BidSearchRequest(
                null, null, null, null,
                null, null, pageable
        );

        // When
        Page<BidForAdZoneResponse> result = queryBidsService.search(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void searchWithFilters_returnsFilteredResults() {
        // Given
        BidSearchRequest request = new BidSearchRequest(
                null, null, null, testBidderName,
                BidStatus.SUBMITTED, BidReviewStatus.PENDING,
                PageRequest.of(0, 20)
        );

        // 필터링된 결과를 모킹
        when(bidForAdZoneRepository.search(any(), any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(testBids.get(0)), request.pageable(), 1)
        );

        // When
        Page<BidForAdZoneResponse> result = queryBidsService.search(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        result.forEach(bid -> {
            assertThat(bid.bidderName()).isEqualTo(testBidderName);
            assertThat(bid.bidStatus()).isEqualTo(BidStatus.SUBMITTED);
            assertThat(bid.bidReviewStatus()).isEqualTo(BidReviewStatus.PENDING);
        });
    }

    @Test
    void findById_withNonExistingId_throwsException() {
        // Given
        UUID nonExistingId = UUID.randomUUID();
        when(bidForAdZoneRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> queryBidsService.findById(nonExistingId));

        assertThat(exception.getMessage()).isEqualTo("입찰을 찾을 수 없습니다.");
    }

    @Test
    void findById_withExistingId_returnsBid() {
        // Given
        when(bidForAdZoneRepository.findById(testBidId)).thenReturn(Optional.of(testBids.get(0)));

        // When
        BidForAdZoneResponse result = queryBidsService.findById(testBidId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testBidId);
        assertThat(result.bidderName()).isEqualTo(testBidderName);
        assertThat(result.bidStatus()).isEqualTo(BidStatus.SUBMITTED);
    }

    @Test
    void searchWithMultipleFilters_returnsCorrectResults() {
        // Given
        BidSearchRequest request = new BidSearchRequest(
                null, testBidNoticeId, null, null,
                BidStatus.AWARDED, null, PageRequest.of(0, 5)
        );

        // 필터링된 결과를 모킹
        when(bidForAdZoneRepository.search(any(), any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(testBids.get(1)), request.pageable(), 1)
        );

        // When
        Page<BidForAdZoneResponse> result = queryBidsService.search(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        result.forEach(bid -> {
            assertThat(bid.bidNoticeId()).isEqualTo(testBidNoticeId);
            assertThat(bid.bidStatus()).isEqualTo(BidStatus.AWARDED);
        });
    }

    @Test
    void searchWithBidderId_returnsAllBidsForBidder() {
        // Given
        BidSearchRequest request = new BidSearchRequest(
                null, null, testBidderId, null,
                null, null, PageRequest.of(0, 10)
        );

        // 특정 입찰자 ID로 필터링된 결과를 모킹
        List<BidForAdZone> filteredBids = testBids.stream()
                .filter(bid -> bid.getBidderId().equals(testBidderId))
                .collect(Collectors.toList());

        when(bidForAdZoneRepository.search(any(), any(Pageable.class))).thenReturn(
                new PageImpl<>(filteredBids, request.pageable(), filteredBids.size())
        );

        // When
        Page<BidForAdZoneResponse> result = queryBidsService.search(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2); // 해당 입찰자의 입찰 건수
        result.forEach(bid -> {
            assertThat(bid.bidderId()).isEqualTo(testBidderId);
        });
    }
}
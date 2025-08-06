package com.example.adservice.application.service;

import com.example.adservice.application.in.dto.BidForAdZoneResponse;
import com.example.adservice.application.in.dto.BidSearchRequest;
import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.model.BidReviewStatus;
import com.example.adservice.domain.model.BidStatus;
import com.example.adservice.domain.model.search.BidSearchCondition;
import com.example.adservice.domain.repository.BidForAdZoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryBidsServiceTest {

    @Mock
    private BidForAdZoneRepository repository;

    @InjectMocks
    private QueryBidsService service;

    @Test
    void searchWithSubmittedStatusAndPendingReview_returnsFilteredResults() {
        // Given
        UUID bidId = UUID.randomUUID();
        UUID bidNoticeId = UUID.randomUUID();
        UUID bidderId = UUID.randomUUID();
        String bidderName = "테스트 입찰자";
        BidSearchRequest request = new BidSearchRequest(
                bidId, bidNoticeId, bidderId, bidderName,
                BidStatus.SUBMITTED, BidReviewStatus.PENDING, Pageable.unpaged()
        );

        BidForAdZone mockBid = createMockBid();
        Page<BidForAdZone> mockPage = new PageImpl<>(List.of(mockBid));

        when(repository.search(any(BidSearchCondition.class), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        Page<BidForAdZoneResponse> result = service.search(request);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchWithCanceledStatusAndRejectedReview_returnsEmptyPage() {
        // Given
        BidSearchRequest request = new BidSearchRequest(
                null, null, null, null,
                BidStatus.CANCELED, BidReviewStatus.REJECTED, Pageable.unpaged()
        );

        Page<BidForAdZone> emptyPage = new PageImpl<>(Collections.emptyList());

        when(repository.search(any(BidSearchCondition.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When
        Page<BidForAdZoneResponse> result = service.search(request);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findExistingBidById_returnsBidResponse() {
        // Given
        UUID id = UUID.randomUUID();
        BidForAdZone mockBid = createMockBid();

        when(repository.findById(id)).thenReturn(Optional.of(mockBid));

        // When
        BidForAdZoneResponse result = service.findById(id);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void findNonExistingBidById_throwsRuntimeException() {
        // Given
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.findById(id));
        assertThat(exception.getMessage()).isEqualTo("입찰을 찾을 수 없습니다.");
    }

    private BidForAdZone createMockBid() {
        return org.mockito.Mockito.mock(BidForAdZone.class);
    }
}
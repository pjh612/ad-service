package com.example.adservice.application.service;

import com.example.adservice.application.in.dto.BidNoticeForAdZoneResponse;
import com.example.adservice.application.in.dto.BidNoticeSearchRequest;
import com.example.adservice.domain.model.BidNoticeForAdZone;
import com.example.adservice.domain.model.BidNoticeState;
import com.example.adservice.domain.model.search.BidNoticeSearchCondition;
import com.example.adservice.domain.repository.BidNoticeForAdZoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class QueryBidNoticeForAdZoneServiceTest {
    @Mock
    private BidNoticeForAdZoneRepository repository;

    @InjectMocks
    private QueryBidNoticeForAdZoneService service;

    private final UUID TEST_USER_ID = UUID.randomUUID();
    private final String TEST_USERNAME = "test-user";

    private BidNoticeForAdZone createSampleNotice() {
        return BidNoticeForAdZone.create(
                "테스트 공고", "내용", TEST_USER_ID, TEST_USERNAME,
                Instant.now().plusSeconds(10), Instant.now().plusSeconds(20),
                Instant.now(), Instant.now().plusSeconds(5),
                1000L, UUID.randomUUID()
        );
    }

    @Test
    void 상태별_공고_검색() {
        // given
        BidNoticeSearchCondition condition = new BidNoticeSearchCondition(
                null,
                null,
                BidNoticeState.BIDDING,
                null,
                null,
                null,
                null
        );
        given(repository.search(eq(condition), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(createSampleNotice())));

        // when
        BidNoticeSearchRequest request = BidNoticeSearchRequest.builder()
                .state(BidNoticeState.BIDDING)
                .pageable(PageRequest.of(0, 10))
                .build();
        Page<BidNoticeForAdZoneResponse> result = service.search(request);

        // then
        assertThat(result.getContent()).hasSize(1);
    }
}

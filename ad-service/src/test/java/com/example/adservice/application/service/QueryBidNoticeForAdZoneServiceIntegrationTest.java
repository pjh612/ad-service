package com.example.adservice.application.service;

import com.example.adservice.TestClockConfiguration;
import com.example.adservice.application.in.dto.BidNoticeForAdZoneResponse;
import com.example.adservice.application.in.dto.BidNoticeSearchRequest;
import com.example.adservice.domain.model.AuditInfo;
import com.example.adservice.domain.model.BidNoticeForAdZone;
import com.example.adservice.domain.model.BidNoticeState;
import com.example.adservice.domain.repository.BidNoticeForAdZoneRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.UUID;


@SpringBootTest
@Import(TestClockConfiguration.class)
public class QueryBidNoticeForAdZoneServiceIntegrationTest {
    @Autowired
    private BidNoticeForAdZoneRepository repository;

    @Autowired
    private QueryBidNoticeForAdZoneService service;

    @Test
    void 입찰공고_검색_테스트() {
        // given
        BidNoticeForAdZone bidNotice = repository.save(createSampleNotice("공고1", BidNoticeState.BIDDING, 2000L));

        // when
        Page<BidNoticeForAdZoneResponse> result = service.search(
                BidNoticeSearchRequest.builder()
                        .state(BidNoticeState.BIDDING)
                        .pageable(PageRequest.of(0, 10))
                        .build()
        );

        // then
        Assertions.assertThat(result.getContent())
                .extracting("id")
                .containsExactly(bidNotice.getId());
    }

    private BidNoticeForAdZone createSampleNotice(String title, BidNoticeState state, long minPrice) {
        return new BidNoticeForAdZone(
                null,
                title,
                "내용",
                UUID.randomUUID(),
                "admin",
                Instant.now().plusSeconds(10),
                Instant.now().plusSeconds(20),
                Instant.now(),
                Instant.now().plusSeconds(5),
                minPrice,
                null,
                null,
                UUID.randomUUID(),
                state,
                AuditInfo.create("admin")
        );
    }
}

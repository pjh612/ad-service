package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.dto.AdZoneResponse;
import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.model.AdZoneState;
import com.example.adadminservice.domain.repository.AdZoneRepository;
import com.example.adadminservice.infrastructure.persistence.AdZoneRepositoryAdapter;
import com.example.adadminservice.infrastructure.persistence.jpa.config.QuerydslConfig;
import com.example.adadminservice.infrastructure.persistence.jpa.repository.AdZoneQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryAdZoneService.class, AdZoneQueryRepository.class, AdZoneRepositoryAdapter.class, QuerydslConfig.class})
class QueryAdZoneServiceIntegrationTest {

    @Autowired
    private QueryAdZoneService queryAdZoneService;

    @Autowired
    private AdZoneRepository adZoneRepository;

    @BeforeEach
    void setUp() {
        List<AdZone> adZones = List.of(
                AdZone.create("메인 배너", "상단 배너", 728, 90),
                AdZone.create("사이드 배너", "좌측 배너", 300, 250),
                AdZone.create("하단 배너", "푸터 영역", 468, 60)
        );

        adZones.forEach(adZone -> adZoneRepository.save(adZone));
    }

    @Test
    @DisplayName("전체 광고 구좌 조회 테스트")
    void getAllAdZones() {
        // when
        Page<AdZoneResponse> result = queryAdZoneService.getAdZones(
                null, null, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("이름으로 필터링된 광고 구좌 조회 테스트")
    void getAdZonesFilteredByName() {
        // when
        Page<AdZoneResponse> result = queryAdZoneService.getAdZones(
                "메인", null, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("메인 배너");
    }

    @Test
    @DisplayName("상태로 필터링된 광고 구좌 조회 테스트")
    void getAdZonesFilteredByState() {
        // given
        AdZone reservedAdZone = AdZone.create("예약 배너", "예약됨", 300, 250);
        reservedAdZone.setAdZoneState(AdZoneState.ADVERTISING);
        adZoneRepository.save(reservedAdZone);

        // when
        Page<AdZoneResponse> result = queryAdZoneService.getAdZones(
                null, AdZoneState.ADVERTISING, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).state()).isEqualTo(AdZoneState.ADVERTISING);
    }
}
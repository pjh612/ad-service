package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.dto.CreateAdZoneRequest;
import com.example.adadminservice.application.in.dto.CreateAdZoneResponse;
import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.repository.AdZoneRepository;
import com.example.adadminservice.infrastructure.persistence.AdZoneRepositoryAdapter;
import com.example.adadminservice.infrastructure.persistence.jpa.config.QuerydslConfig;
import com.example.adadminservice.infrastructure.persistence.jpa.repository.AdZoneQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({CreateAdZoneService.class, AdZoneRepositoryAdapter.class,  AdZoneQueryRepository.class, QuerydslConfig.class})
class CreateAdZoneServiceIntegrationTest {

    @Autowired
    private CreateAdZoneService createAdZoneService;

    @Autowired
    private AdZoneRepository adZoneRepository;

    @Test
    @DisplayName("광고 구좌 생성 및 저장 테스트")
    void createAndSaveAdZone_Success() {
        // given
        CreateAdZoneRequest request = new CreateAdZoneRequest(
                "메인 배너", "상단 배너 위치", 728, 90);

        // when
        CreateAdZoneResponse response = createAdZoneService.create(request);

        // then
        Optional<AdZone> savedAdZone = adZoneRepository.findById(response.id());
        assertThat(savedAdZone).isPresent();
        assertThat(savedAdZone.get().getName()).isEqualTo(request.title());
        assertThat(savedAdZone.get().getWidth()).isEqualTo(request.width());
    }
}

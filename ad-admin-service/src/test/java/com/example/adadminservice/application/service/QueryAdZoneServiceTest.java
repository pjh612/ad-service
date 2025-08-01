package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.dto.AdZoneResponse;
import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.repository.AdZoneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryAdZoneServiceTest {

    @Mock
    private AdZoneRepository adZoneRepository;

    @InjectMocks
    private QueryAdZoneService queryAdZoneService;

    @Test
    @DisplayName("광고 구좌 조회 - 페이징 포함")
    void getAdZones_WithPaging() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<AdZone> adZones = List.of(
                AdZone.create("배너1", "설명1", 300, 250),
                AdZone.create("배너2", "설명2", 728, 90)
        );
        Page<AdZone> mockPage = new PageImpl<>(adZones, pageable, adZones.size());

        when(adZoneRepository.find(anyString(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // when
        Page<AdZoneResponse> result = queryAdZoneService.getAdZones(adZones.get(0).getName(), null, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).name()).isEqualTo("배너1");
        verify(adZoneRepository, times(1)).find(adZones.get(0).getName(), null, pageable);
    }

    @Test
    @DisplayName("이름으로 필터링된 광고 구좌 조회")
    void getAdZones_FilterByName() {
        // given
        String searchName = "메인";
        Pageable pageable = PageRequest.of(0, 10);
        AdZone matchedAdZone = AdZone.create("메인 배너", "설명", 728, 90);
        Page<AdZone> mockPage = new PageImpl<>(List.of(matchedAdZone));

        when(adZoneRepository.find(eq(searchName), isNull(), any(Pageable.class)))
                .thenReturn(mockPage);

        // when
        Page<AdZoneResponse> result = queryAdZoneService.getAdZones(searchName, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("메인 배너");
    }
}
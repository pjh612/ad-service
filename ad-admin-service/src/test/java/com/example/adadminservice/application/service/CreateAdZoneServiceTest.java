package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.dto.CreateAdZoneRequest;
import com.example.adadminservice.application.in.dto.CreateAdZoneResponse;
import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.repository.AdZoneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAdZoneServiceTest {

    @Mock
    private AdZoneRepository adZoneRepository;

    @InjectMocks
    private CreateAdZoneService createAdZoneService;

    @Test
    @DisplayName("광고 구좌 생성 성공 테스트")
    void createAdZone_Success() {
        // given
        CreateAdZoneRequest request = new CreateAdZoneRequest(
                "메인 배너", "상단 배너 위치", 728, 90);

        AdZone mockAdZone = AdZone.create(
                request.title(), request.description(), request.width(), request.height());
        mockAdZone.setId(UUID.randomUUID());

        when(adZoneRepository.save(any(AdZone.class)))
                .thenReturn(mockAdZone);

        // when
        CreateAdZoneResponse response = createAdZoneService.create(request);

        // then
        assertThat(response.id()).isEqualTo(mockAdZone.getId());
        verify(adZoneRepository, times(1)).save(any(AdZone.class));
    }

    @Test
    @DisplayName("유효하지 않은 크기로 생성 시 예외 발생")
    void createAdZone_InvalidSize_ThrowsException() {
        // given
        CreateAdZoneRequest request = new CreateAdZoneRequest(
                "메인 배너", "상단 배너 위치", 0, 90); // width가 0

        // when & then
        assertThatThrownBy(() -> createAdZoneService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구좌 크기는 0 이상이어야 합니다.");

        verify(adZoneRepository, never()).save(any());
    }
}
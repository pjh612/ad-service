package com.example.adadminservice.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdZoneTest {

    // 정상 생성 테스트
    @Test
    void createAdZone_ValidParameters_Success() {
        // when
        AdZone adZone = AdZone.create("메인 배너", "상단 배너 위치", 728, 90);

        // then
        assertThat(adZone.getName()).isEqualTo("메인 배너");
        assertThat(adZone.getWidth()).isEqualTo(728);
        assertThat(adZone.getAdZoneState()).isEqualTo(AdZoneState.EMPTY);
    }

    // 이름 유효성 검사 테스트
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void createAdZone_InvalidName_ThrowsException(String invalidName) {
        // when & then
        assertThatThrownBy(() -> AdZone.create(invalidName, "설명", 300, 250))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구좌 이름을 입력하세요");
    }

    // 설명 유효성 검사 테스트
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void createAdZone_InvalidDescription_ThrowsException(String invalidDescription) {
        // when & then
        assertThatThrownBy(() -> AdZone.create("배너", invalidDescription, 300, 250))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구좌 설명을 입력하세요");
    }

    // 크기 유효성 검사 테스트
    @Test
    void createAdZone_InvalidWidth_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> AdZone.create("배너", "설명", 0, 250))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구좌 크기는 0 이상이어야 합니다.");
    }

    @Test
    void createAdZone_InvalidHeight_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> AdZone.create("배너", "설명", 300, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구좌 크기는 0 이상이어야 합니다.");
    }

    // 상태 변경 테스트 (추가된 경우)
    @Test
    void changeState_ValidState_Success() {
        // given
        AdZone adZone = AdZone.create("배너", "설명", 300, 250);

        // when
        adZone.setAdZoneState(AdZoneState.ADVERTISING);

        // then
        assertThat(adZone.getAdZoneState()).isEqualTo(AdZoneState.ADVERTISING);
    }
}
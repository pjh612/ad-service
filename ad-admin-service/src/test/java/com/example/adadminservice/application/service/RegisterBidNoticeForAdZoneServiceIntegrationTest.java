package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.RegisterBidNoticeForAdZoneUseCase;
import com.example.adadminservice.application.in.dto.RegisterBidNoticeForAdZoneRequest;
import com.example.adadminservice.application.in.dto.RegisterBidNoticeForAdZoneResponse;
import com.example.adadminservice.config.TestClockConfiguration;
import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.model.AdZoneState;
import com.example.adadminservice.domain.model.Admin;
import com.example.adadminservice.domain.model.BidNoticeForAdZone;
import com.example.adadminservice.domain.repository.AdZoneRepository;
import com.example.adadminservice.domain.repository.AdminRepository;
import com.example.adadminservice.domain.repository.BidNoticeForAdZoneRepository;
import com.example.adadminservice.infrastructure.persistence.AdZoneRepositoryAdapter;
import com.example.adadminservice.infrastructure.persistence.AdminRepositoryAdapter;
import com.example.adadminservice.infrastructure.persistence.BidNoticeForAdZoneRepositoryAdapter;
import com.example.adadminservice.infrastructure.persistence.jpa.config.QuerydslConfig;
import com.example.adadminservice.infrastructure.persistence.jpa.repository.AdZoneQueryRepository;
import com.example.adadminservice.infrastructure.persistence.jpa.repository.BidNoticeForAdZoneQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
@Import({TestClockConfiguration.class,QuerydslConfig.class, AdZoneQueryRepository.class, BidNoticeForAdZoneQueryRepository.class,RegisterBidNoticeForAdZoneService.class, AdZoneRepositoryAdapter.class, BidNoticeForAdZoneRepositoryAdapter.class, AdminRepositoryAdapter.class})
class RegisterBidNoticeForAdZoneServiceIntegrationTest {
    @Autowired
    private RegisterBidNoticeForAdZoneUseCase registerBidNoticeForAdZoneUseCase;

    @Autowired
    private AdZoneRepository adZoneRepository;

    @Autowired
    private BidNoticeForAdZoneRepository bidNoticeForAdZoneRepository;


    @Autowired
    private AdminRepository adminRepository;


    @Test
    void 광고_일정_안겹치면_정상등록됨() {
        UUID adZoneId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        AdZone adZone = adZoneRepository.save(new AdZone(adZoneId, "테스트구좌", "설명", 300, 300, AdZoneState.EMPTY));
        Admin admin = adminRepository.save(new Admin(authorId, "test@mail.com", "password", "관", "리자", "개발팀", true, null, LocalDateTime.now(), LocalDateTime.now()));

        adminRepository.save(admin);
        adZoneRepository.save(adZone);

        Instant adStartAt = toInstant(2025, 8, 1, 0, 0, 0);
        Instant adEndAt = toInstant(2025, 8, 7, 23, 59, 59);
        Instant bidStartAt = toInstant(2025, 7, 20, 0, 0, 0);
        Instant bidEndAt = toInstant(2025, 7, 25, 23, 59, 59);

        RegisterBidNoticeForAdZoneRequest request = new RegisterBidNoticeForAdZoneRequest(
                adZoneId, "공고 정상", "설명", authorId, "관리자",
                adStartAt, adEndAt, bidStartAt, bidEndAt, 50000L
        );

        RegisterBidNoticeForAdZoneResponse register = registerBidNoticeForAdZoneUseCase.register(request);

        Assertions.assertThat(register.id()).isNotNull();
    }


    @Test
    void 입찰종료된_광고구좌와_중복_등록시_예외_발생() {
        // given
        UUID adZoneId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        AdZone adZone = adZoneRepository.save(new AdZone(adZoneId, "테스트구좌", "설명", 300, 300, AdZoneState.EMPTY));
        Admin admin = adminRepository.save(new Admin(authorId, "test@mail.com", "password", "관", "리자", "개발팀", true, null, LocalDateTime.now(), LocalDateTime.now()));

        Instant adStartAtA = toInstant(2025, 7, 7, 0, 0, 0);
        Instant adEndAtA = toInstant(2025, 7, 7, 23, 59, 59);
        Instant bidStartAtA = toInstant(2025, 7, 6, 0, 0, 0);
        Instant bidEndAtA = toInstant(2025, 7, 6, 23, 59, 59);

        BidNoticeForAdZone existingBidNotice = BidNoticeForAdZone.create(
                "광고A", "내용", authorId, "관리자", adStartAtA, adEndAtA, bidStartAtA, bidEndAtA, 10000L, adZoneId
        );

        adminRepository.save(admin);
        adZoneRepository.save(adZone);
        bidNoticeForAdZoneRepository.save(existingBidNotice);

        // when & then
        Instant adStartAtB = toInstant(2025, 7, 6, 0, 0, 0);
        Instant adEndAtB = toInstant(2025, 7, 7, 1, 0, 0);
        Instant bidStartAtB = toInstant(2025, 7, 5, 0, 0, 0);
        Instant bidEndAtB = toInstant(2025, 7, 5, 23, 59, 59);

        RegisterBidNoticeForAdZoneRequest request = new RegisterBidNoticeForAdZoneRequest(
                adZoneId, "공고B", "공고B", authorId, "관리자",
                adStartAtB, adEndAtB, bidStartAtB, bidEndAtB, 10000L
        );

        Assertions.assertThatThrownBy(() -> registerBidNoticeForAdZoneUseCase.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 구좌는 지정한 기간에 이미 광고 예정이거나 광고 중입니다.");
    }

    @Test
    void 광고_시작일이_종료일보다_늦으면_예외발생() {
        UUID adZoneId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        adZoneRepository.save(new AdZone(adZoneId, "테스트구좌", "설명", 300, 300, AdZoneState.EMPTY));
        adminRepository.save(new Admin(authorId, "test@mail.com", "password", "관", "리자", "개발팀", true, null, LocalDateTime.now(), LocalDateTime.now()));

        Instant adStartAt = toInstant(2025, 8, 10, 0, 0, 0);
        Instant adEndAt = toInstant(2025, 8, 5, 23, 59, 59); // 잘못된 순서
        Instant bidStartAt = toInstant(2025, 7, 20, 0, 0, 0);
        Instant bidEndAt = toInstant(2025, 7, 25, 23, 59, 59);

        RegisterBidNoticeForAdZoneRequest request = new RegisterBidNoticeForAdZoneRequest(
                adZoneId, "잘못된 날짜", "광고 시작 > 종료", authorId, "관리자",
                adStartAt, adEndAt, bidStartAt, bidEndAt, 10000L
        );

        assertThatThrownBy(() -> registerBidNoticeForAdZoneUseCase.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("광고 시작일은 광고 종료일보다 빠르거나 같아야 합니다.");
    }

    @Test
    void 입찰_시작일이_종료일보다_늦으면_예외발생() {
        UUID adZoneId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        adZoneRepository.save(new AdZone(adZoneId, "테스트구좌", "설명", 300, 300, AdZoneState.EMPTY));
        adminRepository.save(new Admin(authorId, "test@mail.com", "password", "관", "리자", "개발팀", true, null, LocalDateTime.now(), LocalDateTime.now()));

        Instant adStartAt = toInstant(2025, 8, 5, 0, 0, 0);
        Instant adEndAt = toInstant(2025, 8, 10, 23, 59, 59);
        Instant bidStartAt = toInstant(2025, 7, 26, 0, 0, 0);
        Instant bidEndAt = toInstant(2025, 7, 25, 23, 59, 59); // 잘못된 순서

        RegisterBidNoticeForAdZoneRequest request = new RegisterBidNoticeForAdZoneRequest(
                adZoneId, "입찰시작 > 종료", "입찰 잘못된 날짜", authorId, "관리자",
                adStartAt, adEndAt, bidStartAt, bidEndAt, 10000L
        );

        assertThatThrownBy(() -> registerBidNoticeForAdZoneUseCase.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입찰 시작일은 입찰 종료일보다 빠르거나 같아야 합니다.");
    }

    @Test
    void 입찰_종료일이_광고_시작일_이후면_예외발생() {
        UUID adZoneId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        adZoneRepository.save(new AdZone(adZoneId, "테스트구좌", "설명", 300, 300, AdZoneState.EMPTY));
        adminRepository.save(new Admin(authorId, "test@mail.com", "password", "관", "리자", "개발팀", true, null, LocalDateTime.now(), LocalDateTime.now()));


        Instant adStartAt = toInstant(2025, 8, 5, 0, 0, 0);
        Instant adEndAt = toInstant(2025, 8, 10, 23, 59, 59);
        Instant bidStartAt = toInstant(2025, 8, 1, 0, 0, 0);
        Instant bidEndAt = toInstant(2025, 8, 6, 0, 0, 0); // 광고 시작일 이후

        RegisterBidNoticeForAdZoneRequest request = new RegisterBidNoticeForAdZoneRequest(
                adZoneId, "입찰종료 > 광고시작", "위반조건", authorId, "관리자",
                adStartAt, adEndAt, bidStartAt, bidEndAt, 10000L
        );

        assertThatThrownBy(() -> registerBidNoticeForAdZoneUseCase.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입찰 마감일은 광고 시작일보다 이전이어야 합니다.");
    }

    @Test
    void 존재하지않는_광고구좌로_등록시_예외발생() {
        UUID notExistAdZoneId = UUID.randomUUID();
        UUID notExistAuthorId = UUID.randomUUID();

        Instant adStartAt = toInstant(2025, 8, 5, 0, 0, 0);
        Instant adEndAt = toInstant(2025, 8, 10, 23, 59, 59);
        Instant bidStartAt = toInstant(2025, 7, 25, 0, 0, 0);
        Instant bidEndAt = toInstant(2025, 7, 29, 23, 59, 59);

        RegisterBidNoticeForAdZoneRequest request = new RegisterBidNoticeForAdZoneRequest(
                notExistAdZoneId, "존재하지않는구좌", "없음", notExistAuthorId, "미상",
                adStartAt, adEndAt, bidStartAt, bidEndAt, 10000L
        );

        assertThatThrownBy(() -> registerBidNoticeForAdZoneUseCase.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 광고 구좌 입니다.");
    }

    @Test
    void 제목이_빈값이면_예외발생() {
        UUID adZoneId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        adZoneRepository.save(new AdZone(adZoneId, "테스트구좌", "설명", 300, 300, AdZoneState.EMPTY));
        adminRepository.save(new Admin(authorId, "test@mail.com", "password", "관", "리자", "개발팀", true, null, LocalDateTime.now(), LocalDateTime.now()));

        RegisterBidNoticeForAdZoneRequest request = new RegisterBidNoticeForAdZoneRequest(
                adZoneId, "", "내용", authorId, "관리자",
                toInstant(2025, 8, 5, 0, 0, 0),
                toInstant(2025, 8, 10, 23, 59, 59),
                toInstant(2025, 7, 20, 0, 0, 0),
                toInstant(2025, 7, 25, 23, 59, 59),
                10000L
        );

        assertThatThrownBy(() -> registerBidNoticeForAdZoneUseCase.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title must not be blank");
    }

    private Instant toInstant(int year, int month, int day, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, day, hour, minute, second).atZone(ZoneId.systemDefault()).toInstant();
    }
}
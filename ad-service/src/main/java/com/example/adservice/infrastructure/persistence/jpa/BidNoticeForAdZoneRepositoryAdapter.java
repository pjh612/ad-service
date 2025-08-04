package com.example.adservice.infrastructure.persistence.jpa;


import com.example.adservice.domain.model.AuditInfo;
import com.example.adservice.domain.model.BidNoticeForAdZone;
import com.example.adservice.domain.model.search.BidNoticeSearchCondition;
import com.example.adservice.domain.repository.BidNoticeForAdZoneRepository;
import com.example.adservice.infrastructure.persistence.jpa.entity.BidNoticeForAdZoneEntity;
import com.example.adservice.infrastructure.persistence.jpa.repository.BidNoticeForAdZoneJpaRepository;
import com.example.adservice.infrastructure.persistence.jpa.repository.BidNoticeForAdZoneQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BidNoticeForAdZoneRepositoryAdapter implements BidNoticeForAdZoneRepository {

    private final BidNoticeForAdZoneJpaRepository jpaRepository;
    private final BidNoticeForAdZoneQueryRepository queryRepository;

    @Override
    public Optional<BidNoticeForAdZone> findById(UUID id) {
        return jpaRepository.findById(id).map(BidNoticeMapper::toDomain);
    }

    @Override
    public boolean existsActiveBidNoticeByAdZoneIdAndOverlappingPeriod(UUID id, Instant adStartAt, Instant adEndAt) {
        return queryRepository.existsActiveBidNoticeByAdZoneIdAndOverlappingPeriod(id, adStartAt, adEndAt);
    }

    @Override
    public List<BidNoticeForAdZone> findByZoneId(UUID zoneId) {
        return jpaRepository.findByZoneId(zoneId)
                .stream()
                .map(BidNoticeMapper::toDomain)
                .toList();
    }

    @Override
    public Page<BidNoticeForAdZone> search(BidNoticeSearchCondition condition, Pageable pageable) {
        return queryRepository.search(condition, pageable)
                .map(BidNoticeMapper::toDomain);
    }

    @Override
    public BidNoticeForAdZone save(BidNoticeForAdZone notice) {
        BidNoticeForAdZoneEntity entity = BidNoticeMapper.toEntity(notice);
        BidNoticeForAdZoneEntity saved = jpaRepository.save(entity);
        return BidNoticeMapper.toDomain(saved);
    }

    static class BidNoticeMapper {

        static BidNoticeForAdZone toDomain(BidNoticeForAdZoneEntity entity) {
            return new BidNoticeForAdZone(
                    entity.getId(),
                    entity.getTitle(),
                    entity.getContent(),
                    entity.getAuthorId(),
                    entity.getAuthorName(),
                    entity.getAdStartAt(),
                    entity.getAdEndAt(),
                    entity.getBidStartAt(),
                    entity.getBidEndAt(),
                    entity.getMinPrice(),
                    entity.getCurrentBidAmount(),
                    entity.getCurrentBidderId(),
                    entity.getZoneId(),
                    entity.getBidNoticeState(),
                    new AuditInfo(entity.getCreatedAt(), entity.getCreatedBy(), entity.getUpdatedAt(), entity.getUpdatedBy())
            );
        }

        static BidNoticeForAdZoneEntity toEntity(BidNoticeForAdZone domain) {
            return new BidNoticeForAdZoneEntity(
                    domain.getId(),
                    domain.getTitle(),
                    domain.getContent(),
                    domain.getAuthorId(),
                    domain.getAuthorName(),
                    domain.getAdStartAt(),
                    domain.getAdEndAt(),
                    domain.getBidStartAt(),
                    domain.getBidEndAt(),
                    domain.getMinPrice(),
                    domain.getCurrentBidAmount(),
                    domain.getCurrentBidderId(),
                    domain.getZoneId(),
                    domain.getBidNoticeState(),
                    domain.getAuditInfo().getCreatedAt(),
                    domain.getAuditInfo().getUpdatedAt(),
                    domain.getAuditInfo().getCreatedBy(),
                    domain.getAuditInfo().getUpdatedBy()
            );
        }
    }
}

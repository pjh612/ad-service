package com.example.adservice.infrastructure.persistence.jpa.adapter;

import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.model.search.BidSearchCondition;
import com.example.adservice.domain.repository.BidForAdZoneRepository;
import com.example.adservice.infrastructure.persistence.jpa.mapper.BidForAdZoneEntityMapper;
import com.example.adservice.infrastructure.persistence.jpa.repository.BidForAdZoneJpaRepository;
import com.example.adservice.infrastructure.persistence.jpa.repository.BidForAdZoneQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BidForAdZoneRepositoryAdapter implements BidForAdZoneRepository {

    private final BidForAdZoneJpaRepository jpaRepository;
    private final BidForAdZoneQueryRepository queryRepository;

    @Override
    public BidForAdZone save(BidForAdZone bidForAdZone) {
        return BidForAdZoneEntityMapper.toDomain(
                jpaRepository.save(BidForAdZoneEntityMapper.toEntity(bidForAdZone))
        );
    }

    @Override
    public Page<BidForAdZone> search(BidSearchCondition condition, Pageable pageable) {
        return queryRepository.search(condition, pageable)
                .map(BidForAdZoneEntityMapper::toDomain);
    }

    @Override
    public Optional<BidForAdZone> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(BidForAdZoneEntityMapper::toDomain);
    }
}

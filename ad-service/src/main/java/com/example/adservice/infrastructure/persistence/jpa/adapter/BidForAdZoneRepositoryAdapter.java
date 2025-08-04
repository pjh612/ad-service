package com.example.adservice.infrastructure.persistence.jpa.adapter;

import com.example.adservice.domain.model.BidForAdZone;
import com.example.adservice.domain.repository.BidForAdZoneRepository;
import com.example.adservice.infrastructure.persistence.jpa.mapper.BidForAdZoneEntityMapper;
import com.example.adservice.infrastructure.persistence.jpa.repository.BidForAdZoneJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BidForAdZoneRepositoryAdapter implements BidForAdZoneRepository {

    private final BidForAdZoneJpaRepository jpaRepository;

    @Override
    public BidForAdZone save(BidForAdZone bidForAdZone) {
        return BidForAdZoneEntityMapper.toDomain(
                jpaRepository.save(BidForAdZoneEntityMapper.toEntity(bidForAdZone))
        );
    }
}

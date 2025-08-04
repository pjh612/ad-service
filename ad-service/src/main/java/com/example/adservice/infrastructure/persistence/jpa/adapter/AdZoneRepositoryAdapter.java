package com.example.adservice.infrastructure.persistence.jpa.adapter;

import com.example.adservice.domain.model.AdZone;
import com.example.adservice.domain.model.AuditInfo;
import com.example.adservice.domain.repository.AdZoneRepository;
import com.example.adservice.infrastructure.persistence.jpa.entity.AdZoneEntity;
import com.example.adservice.infrastructure.persistence.jpa.repository.AdZoneJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AdZoneRepositoryAdapter implements AdZoneRepository {
    private final AdZoneJpaRepository adZoneJpaRepository;

    public AdZoneRepositoryAdapter(AdZoneJpaRepository adZoneJpaRepository) {
        this.adZoneJpaRepository = adZoneJpaRepository;
    }

    @Override
    public Optional<AdZone> findById(UUID id) {
        return adZoneJpaRepository.findById(id).map(AdZoneMapper::toDomain);
    }

    @Override
    public AdZone save(AdZone adZone) {
        AdZoneEntity entity = AdZoneMapper.toEntity(adZone);
        AdZoneEntity saved = adZoneJpaRepository.save(entity);
        return AdZoneMapper.toDomain(saved);
    }

    static class AdZoneMapper {
        static AdZone toDomain(AdZoneEntity entity) {
            return new AdZone(
                    entity.getId(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getWidth(),
                    entity.getHeight(),
                    entity.getAdZoneState(),
                    new AuditInfo(entity.getCreatedAt(), entity.getCreatedBy(), entity.getUpdatedAt(), entity.getUpdatedBy())
            );
        }

        static AdZoneEntity toEntity(AdZone domain) {
            return new AdZoneEntity(
                    domain.getId(),
                    domain.getName(),
                    domain.getDescription(),
                    domain.getWidth(),
                    domain.getHeight(),
                    domain.getAdZoneState(),
                    domain.getAuditInfo().getCreatedAt(),
                    domain.getAuditInfo().getUpdatedAt(),
                    domain.getAuditInfo().getCreatedBy(),
                    domain.getAuditInfo().getUpdatedBy()
            );
        }
    }
}

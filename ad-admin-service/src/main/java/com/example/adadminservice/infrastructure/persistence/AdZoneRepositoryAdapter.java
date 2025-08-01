package com.example.adadminservice.infrastructure.persistence;

import com.example.adadminservice.domain.model.AdZone;
import com.example.adadminservice.domain.model.AdZoneState;
import com.example.adadminservice.domain.repository.AdZoneRepository;
import com.example.adadminservice.infrastructure.persistence.jpa.entity.AdZoneEntity;
import com.example.adadminservice.infrastructure.persistence.jpa.repository.AdZoneJpaRepository;
import com.example.adadminservice.infrastructure.persistence.jpa.repository.AdZoneQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdZoneRepositoryAdapter implements AdZoneRepository {
    private final AdZoneJpaRepository adZoneJpaRepository;
    private final AdZoneQueryRepository adZoneQueryRepository;

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

    @Override
    public Page<AdZone> find(String name, AdZoneState state, Pageable pageable) {
        return adZoneQueryRepository.find(name, state, pageable)
                .map(AdZoneMapper::toDomain);
    }


    static class AdZoneMapper {
        static AdZone toDomain(AdZoneEntity entity) {
            return new AdZone(
                    entity.getId(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getWidth(),
                    entity.getHeight(),
                    entity.getAdZoneState()
            );
        }

        static AdZoneEntity toEntity(AdZone domain) {
            return new AdZoneEntity(
                    domain.getId(),
                    domain.getName(),
                    domain.getDescription(),
                    domain.getWidth(),
                    domain.getHeight(),
                    domain.getAdZoneState()
            );
        }
    }
}

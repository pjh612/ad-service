package com.example.adservice.infrastructure.persistence.jpa.adapter;

import com.example.adservice.domain.model.Advertiser;
import com.example.adservice.domain.model.AuditInfo;
import com.example.adservice.domain.repository.AdvertiserRepository;
import com.example.adservice.infrastructure.persistence.jpa.entity.AdvertiserEntity;
import com.example.adservice.infrastructure.persistence.jpa.repository.AdvertiserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AdvertiserRepositoryAdapter implements AdvertiserRepository {

    private final AdvertiserJpaRepository advertiserJpaRepository;

    public AdvertiserRepositoryAdapter(AdvertiserJpaRepository advertiserJpaRepository) {
        this.advertiserJpaRepository = advertiserJpaRepository;
    }

    @Override
    public Optional<Advertiser> findById(UUID id) {
        return advertiserJpaRepository.findById(id).map(AdvertiserMapper::toDomain);
    }

    @Override
    public Advertiser save(Advertiser advertiser) {
        AdvertiserEntity entity = AdvertiserMapper.toEntity(advertiser);
        AdvertiserEntity saved = advertiserJpaRepository.save(entity);
        return AdvertiserMapper.toDomain(saved);
    }

    static class AdvertiserMapper {
        static Advertiser toDomain(AdvertiserEntity entity) {
            return new Advertiser(
                    entity.getId(),
                    entity.getUsername(),
                    entity.getPassword(),
                    entity.getName(),
                    entity.getEmail(),
                    entity.getBusinessNumber(),
                    entity.getStartAt(),
                    new AuditInfo(entity.getCreatedAt(), entity.getCreatedBy(), entity.getUpdatedAt(), entity.getUpdatedBy())
            );
        }

        static AdvertiserEntity toEntity(Advertiser advertiser) {
            return new AdvertiserEntity(
                    advertiser.getId(),
                    advertiser.getUsername(),
                    advertiser.getPassword(),
                    advertiser.getName(),
                    advertiser.getEmail(),
                    advertiser.getBusinessNumber(),
                    advertiser.getStartAt(),
                    advertiser.getAuditInfo().getCreatedAt(),
                    advertiser.getAuditInfo().getUpdatedAt(),
                    advertiser.getAuditInfo().getCreatedBy(),
                    advertiser.getAuditInfo().getUpdatedBy()
            );
        }
    }
}

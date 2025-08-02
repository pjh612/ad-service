package com.example.adadminservice.infrastructure.persistence.jpa.repository;

import com.example.adadminservice.domain.model.BidNoticeState;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

import static com.example.adadminservice.infrastructure.persistence.jpa.entity.QAdZoneEntity.adZoneEntity;
import static com.example.adadminservice.infrastructure.persistence.jpa.entity.QBidNoticeForAdZoneEntity.bidNoticeForAdZoneEntity;


@Component
public class BidNoticeForAdZoneQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public BidNoticeForAdZoneQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public boolean existsActiveBidNoticeByAdZoneIdAndOverlappingPeriod(UUID adZoneId, Instant adStartAt, Instant adEndAt) {
        return jpaQueryFactory.select(bidNoticeForAdZoneEntity.count())
                .from(bidNoticeForAdZoneEntity)
                .join(adZoneEntity).on(bidNoticeForAdZoneEntity.zoneId.eq(adZoneEntity.id))
                .where(adZoneEntity.id.eq(adZoneId),
                        bidNoticeForAdZoneEntity.adStartAt.before(adEndAt),
                        bidNoticeForAdZoneEntity.adEndAt.after(adStartAt),
                        bidNoticeForAdZoneEntity.bidNoticeState.ne(BidNoticeState.CANCELED)
                )
                .fetchOne() > 0;
    }
}

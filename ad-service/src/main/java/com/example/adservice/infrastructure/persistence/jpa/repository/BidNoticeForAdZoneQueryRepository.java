package com.example.adservice.infrastructure.persistence.jpa.repository;

import com.example.adservice.domain.model.BidNoticeState;
import com.example.adservice.domain.model.search.BidNoticeSearchCondition;
import com.example.adservice.infrastructure.persistence.jpa.entity.BidNoticeForAdZoneEntity;
import com.example.adservice.infrastructure.persistence.jpa.entity.QBidNoticeForAdZoneEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.adservice.infrastructure.persistence.jpa.entity.QAdZoneEntity.adZoneEntity;
import static com.example.adservice.infrastructure.persistence.jpa.entity.QBidNoticeForAdZoneEntity.bidNoticeForAdZoneEntity;

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

    public Page<BidNoticeForAdZoneEntity> search(BidNoticeSearchCondition condition, Pageable pageable) {
        // 1. QueryDSL 쿼리 생성
        List<BidNoticeForAdZoneEntity> content = jpaQueryFactory
                .selectFrom(bidNoticeForAdZoneEntity)
                .where(
                        titleContains(condition.title()),
                        authorNameEq(condition.authorName()),
                        stateEq(condition.state()),
                        adStartBetween(condition.adStartFrom(), condition.adStartTo()),
                        minPriceGoe(condition.minPrice()),
                        zoneIdEq(condition.zoneId())
                )
                .orderBy(bidNoticeForAdZoneEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(bidNoticeForAdZoneEntity.count())
                .from(bidNoticeForAdZoneEntity)
                .where(
                        titleContains(condition.title()),
                        authorNameEq(condition.authorName()),
                        stateEq(condition.state()),
                        adStartBetween(condition.adStartFrom(), condition.adStartTo()),
                        minPriceGoe(condition.minPrice()),
                        zoneIdEq(condition.zoneId())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? bidNoticeForAdZoneEntity.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression authorNameEq(String authorName) {
        return StringUtils.hasText(authorName) ? bidNoticeForAdZoneEntity.authorName.eq(authorName) : null;
    }

    private BooleanExpression stateEq(BidNoticeState state) {
        return state != null ? bidNoticeForAdZoneEntity.bidNoticeState.eq(state) : null;
    }

    private BooleanExpression adStartBetween(Instant from, Instant to) {
        if (from == null && to == null) return null;
        QBidNoticeForAdZoneEntity q = bidNoticeForAdZoneEntity;
        if (from != null && to != null) return q.adStartAt.between(from, to);
        if (from != null) return q.adStartAt.goe(from);

        return q.adStartAt.loe(to);
    }

    private BooleanExpression minPriceGoe(Long minPrice) {
        return minPrice != null ? bidNoticeForAdZoneEntity.minPrice.goe(minPrice) : null;
    }

    private BooleanExpression zoneIdEq(UUID zoneId) {
        return zoneId != null ? bidNoticeForAdZoneEntity.zoneId.eq(zoneId) : null;
    }
}

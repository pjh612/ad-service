package com.example.adservice.infrastructure.persistence.jpa.repository;

import com.example.adservice.domain.model.BidReviewStatus;
import com.example.adservice.domain.model.BidStatus;
import com.example.adservice.domain.model.search.BidSearchCondition;
import com.example.adservice.infrastructure.persistence.jpa.entity.BidForAdZoneEntity;
import com.example.adservice.infrastructure.persistence.jpa.entity.QBidForAdZoneEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BidForAdZoneQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public BidForAdZoneQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<BidForAdZoneEntity> search(BidSearchCondition condition, Pageable pageable) {
        QBidForAdZoneEntity bid = QBidForAdZoneEntity.bidForAdZoneEntity;

        List<BidForAdZoneEntity> content = jpaQueryFactory
                .selectFrom(bid)
                .where(
                        idEq(condition.id()),
                        bidNoticeIdEq(condition.bidNoticeId()),
                        bidderIdEq(condition.bidderId()),
                        bidderNameEq(condition.bidderName()),
                        bidStatusEq(condition.bidStatus()),
                        bidReviewStatusEq(condition.bidReviewStatus())
                )
                .orderBy(bid.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(bid.count())
                .from(bid)
                .where(
                        idEq(condition.id()),
                        bidNoticeIdEq(condition.bidNoticeId()),
                        bidderIdEq(condition.bidderId()),
                        bidderNameEq(condition.bidderName()),
                        bidStatusEq(condition.bidStatus()),
                        bidReviewStatusEq(condition.bidReviewStatus())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression idEq(UUID id) {
        return id != null ? QBidForAdZoneEntity.bidForAdZoneEntity.id.eq(id) : null;
    }

    private BooleanExpression bidNoticeIdEq(UUID bidNoticeId) {
        return bidNoticeId != null ? QBidForAdZoneEntity.bidForAdZoneEntity.bidNoticeId.eq(bidNoticeId) : null;
    }

    private BooleanExpression bidderIdEq(UUID bidderId) {
        return bidderId != null ? QBidForAdZoneEntity.bidForAdZoneEntity.bidderId.eq(bidderId) : null;
    }

    private BooleanExpression bidderNameEq(String bidderName) {
        return bidderName != null ? QBidForAdZoneEntity.bidForAdZoneEntity.bidderName.eq(bidderName) : null;
    }

    private BooleanExpression bidStatusEq(BidStatus bidStatus) {
        return bidStatus != null ? QBidForAdZoneEntity.bidForAdZoneEntity.bidStatus.eq(bidStatus) : null;
    }

    private BooleanExpression bidReviewStatusEq(BidReviewStatus bidReviewStatus) {
        return bidReviewStatus != null ? QBidForAdZoneEntity.bidForAdZoneEntity.bidReviewStatus.eq(bidReviewStatus) : null;
    }
}
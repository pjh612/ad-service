package com.example.adadminservice.infrastructure.persistence.jpa.repository;

import com.example.adadminservice.domain.model.AdZoneState;
import com.example.adadminservice.infrastructure.persistence.jpa.entity.AdZoneEntity;
import com.example.adadminservice.infrastructure.persistence.jpa.entity.QAdZoneEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.adadminservice.infrastructure.persistence.jpa.entity.QAdZoneEntity.adZoneEntity;

@Component
@RequiredArgsConstructor
public class AdZoneQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<AdZoneEntity> find(String name, AdZoneState state, Pageable pageable) {
        JPAQuery<AdZoneEntity> query = queryFactory
                .selectFrom(adZoneEntity)
                .where(
                        nameContains(name),
                        stateEquals(state)
                );

        long total = query.fetchCount();

        List<AdZoneEntity> content = query
                .orderBy(getOrderSpecifiers(pageable.getSort(), adZoneEntity))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ?
                adZoneEntity.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression stateEquals(AdZoneState state) {
        return state != null ?
                adZoneEntity.adZoneState.eq(state) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort, QAdZoneEntity adZoneEntity) {
        return sort.stream()
                .map(order -> {
                    String property = order.getProperty();
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    return switch (property) {
                        case "name" -> new OrderSpecifier<>(direction, adZoneEntity.name);
                        default -> new OrderSpecifier<>(direction, adZoneEntity.id);
                    };
                })
                .toArray(OrderSpecifier[]::new);
    }
}

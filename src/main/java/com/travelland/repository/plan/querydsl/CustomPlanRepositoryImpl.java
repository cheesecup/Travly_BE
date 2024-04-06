package com.travelland.repository.plan.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.plan.Plan;
import com.travelland.dto.PlanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.plan.QPlan.plan;

@Repository
@RequiredArgsConstructor
public class CustomPlanRepositoryImpl implements CustomPlanRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PlanDto.GetList> getPlanList(Long lastId, int size, String sortBy, boolean isAsc) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                PlanDto.GetList.class,
                                plan.id,
                                plan.title,
                                plan.viewCount,
                                plan.createdAt
                        )
                )
                .from(plan)
                .where(ltPlanId(lastId))
                .orderBy(createOrderSpecifier(sortBy, isAsc))
                .limit(size)
                .fetch();
    }
    private BooleanExpression ltPlanId(Long planId) {
        if (planId == null)
            return null;

        return plan.id.lt(planId);
    }

    private OrderSpecifier createOrderSpecifier(String sort, boolean ASC) {
        Order order = (ASC) ? Order.ASC : Order.DESC;

        return switch (sort) {
            case "viewCount" -> new OrderSpecifier<>(order, plan.viewCount);
            case "title" -> new OrderSpecifier<>(order, plan.title);
            default -> new OrderSpecifier<>(order, plan.createdAt);
        };
    }
}

package com.travelland.repository.plan.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.plan.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.plan.QPlan.plan;

@Repository
@RequiredArgsConstructor
public class CustomPlanRepositoryImpl implements CustomPlanRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Plan> getPlanList(int page, int size, String sort, boolean ASC) {
        OrderSpecifier orderSpecifier = createOrderSpecifier(sort, ASC);

        return jpaQueryFactory.selectFrom(plan)
                .orderBy(orderSpecifier, plan.id.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
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

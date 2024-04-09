package com.travelland.repository.plan.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.member.Member;
import com.travelland.domain.plan.PlanLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.plan.QPlanLike.planLike;

@Repository
@RequiredArgsConstructor
public class CustomPlanLikeRepositoryImpl implements CustomPlanLikeRepository{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<PlanLike> getLikeListByMember(Member member, int size, int page){
        return  jpaQueryFactory.selectFrom(planLike)
                .where(planLike.member.eq(member), planLike.isDeleted.eq(false))
                .orderBy(planLike.plan.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }
}

package com.travelland.repository.plan.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.member.Member;
import com.travelland.domain.plan.PlanScrap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.plan.QPlanScrap.planScrap;

@Repository
@RequiredArgsConstructor
public class CustomPlanScrapRepositoryImpl implements CustomPlanScrapRepository{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<PlanScrap> getScrapListByMember(Member member, int size, int page){
        return  jpaQueryFactory.selectFrom(planScrap)
                .where(planScrap.member.eq(member), planScrap.isDeleted.eq(false))
                .orderBy(planScrap.plan.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }
}

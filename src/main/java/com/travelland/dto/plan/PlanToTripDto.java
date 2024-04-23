package com.travelland.dto.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PlanToTripDto {
//    private Long id;
    private final String title;
    private final String content;
    private final int cost;
    private final String area; //여행 지역
//    private final String address; //도로명 주소
//    private String placeName; //여행장소
//    private final boolean isPublic;
    private final LocalDate tripStartDate;
    private final LocalDate tripEndDate;
//    private final int viewCount;
//    private final int likeCount;
//    private final boolean isDeleted;
//    private final Member member;
//    private final LocalDateTime createdAt;
//    private final LocalDateTime modifiedAt;

    @Builder
    public PlanToTripDto(Plan plan, String content) {
//        this.id =
        this.title = plan.getTitle();
        this.content = content;
        this.cost = plan.getBudget();
        this.area = plan.getArea();
//        this.address = //도로명 주소
//        this.placeName =
//        this.isPublic =
        this.tripStartDate = plan.getTripStartDate();
        this.tripEndDate = plan.getTripEndDate();
//        this.viewCount =
//        this.likeCount =
//        this.isDeleted =
//        this.member =
//        this.createdAt =
//        this.modifiedAt =
    }
}

//@Getter
//public static class GetAllInOne {
//    private final Long planId;
//    private final String title; // -> title
////        private final String content;
//    private final int budget; // -> cost
//    private final String area; // -> area
//    private final Boolean isPublic; // -> isPublic
//    private final LocalDate tripStartDate; // -> tripStartDate
//    private final LocalDate tripEndDate; // -> tripEndDate
//    private final int viewCount; // 매칭대상 있지만 연동 x
//    private final int likeCount; // 매칭대상 있지만 연동 x
//    private final Boolean isVotable; // 매칭대상 없음
//    private final LocalDateTime createdAt; // 매칭대상 있지만 연동 x
//    private final String memberNickname; // 이건 시큐리티가
//    private final String profileUrl; // 이건 시큐리티가
//    private List<DayPlanDto.GetAllInOne> dayPlans; // -> content
//    private List<PlanVoteDto.GetAllInOne> planVotes; // -> content ?
//    private final Boolean isLike;
//    private final Boolean isScrap;
//}
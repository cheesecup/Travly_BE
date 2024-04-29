package com.travelland.domain.plan;

import com.travelland.domain.member.Member;
import com.travelland.dto.plan.PlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    private LocalDate tripStartDate;

    private LocalDate tripEndDate;

    @Column(length = 30)
    private String area;

    private int budget;

    private Boolean isPublic;

    private Boolean isVotable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    private int viewCount = 0;

    private int likeCount = 0;

    private Boolean isDeleted = false;

    public Plan(PlanDto.CreateAllInOne request, Member member) {
        this.title = request.getTitle();
        this.tripStartDate = request.getTripStartDate();
        this.tripEndDate = request.getTripEndDate();
        this.area = request.getArea();
        this.budget = request.getBudget();
        this.isPublic = request.getIsPublic();
        this.isVotable = request.getIsVotable();
        this.member = member;

        checkIsPastDate();
    }

    public Plan update(PlanDto.UpdateAllInOne request) {
        this.title = request.getTitle();
        this.tripStartDate = request.getTripStartDate();
        this.tripEndDate = request.getTripEndDate();
        this.area = request.getArea();
        this.budget = request.getBudget();
        this.isPublic = request.getIsPublic();
        this.isVotable = request.getIsVotable();

        checkIsPastDate();

        return this;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    // 입력된기간 (tripStartDate/tripEndDate)가 현재보다 과거시각인지 확인
    public void checkIsPastDate() {
        LocalDate now = LocalDate.now();
        if (tripStartDate.isBefore(now)) {
            throw new CustomException(ErrorCode.WRONG_PLAN_DATE);
        }
    }
}

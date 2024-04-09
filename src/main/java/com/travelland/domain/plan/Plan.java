package com.travelland.domain.plan;

import com.travelland.domain.member.Member;
import com.travelland.dto.PlanDto;
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

    private String title;

    private String content;

    private int budget;

    private String area;

    private Boolean isPublic;

    private LocalDate tripStartDate;

    private LocalDate tripEndDate;

    private int viewCount;

    private int likeCount;

    private Boolean isVotable;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Boolean isDeleted = false;

    public Plan(PlanDto.Create request, Member member) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.area = request.getArea();
        this.isPublic = request.getIsPublic();
        this.tripStartDate = request.getTripStartDate();
        this.tripEndDate = request.getTripEndDate();
        this.viewCount = 0;
        this.likeCount = 0;
        this.isVotable = request.getIsVotable();
        this.member = member;
    }

    public Plan(PlanDto.CreateAllInOne request, Member member) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.area = request.getArea();
        this.isPublic = request.getIsPublic();
        this.tripStartDate = request.getTripStartDate();
        this.tripEndDate = request.getTripEndDate();
        this.viewCount = 0;
        this.likeCount = 0;
        this.isVotable = request.getIsVotable();
        this.member = member;
    }

    public Plan update(PlanDto.Update request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.area = request.getArea();
        this.isPublic = request.getIsPublic();
        this.tripStartDate = request.getTripStartDate();
        this.tripEndDate = request.getTripEndDate();
        this.isVotable = request.getIsVotable();

        return this;
    }

    public Plan update(PlanDto.UpdateAllInOne request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.area = request.getArea();
        this.isPublic = request.getIsPublic();
        this.tripStartDate = request.getTripStartDate();
        this.tripEndDate = request.getTripEndDate();
        this.isVotable = request.getIsVotable();

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
}

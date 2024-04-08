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

    private boolean isPublic;

    private LocalDate tripStartDate;

    private LocalDate tripEndDate;

    private int viewCount;

    private int likeCount;

    private boolean isVotable;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Plan(PlanDto.Create requestDto, Member member) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.budget = requestDto.getBudget();
        this.area = requestDto.getArea();
        this.isPublic = requestDto.isPublic();
        this.tripStartDate = requestDto.getTripStartDate();
        this.tripEndDate = requestDto.getTripEndDate();
        this.viewCount = 0;
        this.likeCount = 0;
        this.isVotable = requestDto.isVotable();
        this.member = member;
    }

    public Plan update(PlanDto.Update request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();;
        this.area = request.getArea();
        this.isPublic = request.isPublic();;
        this.tripStartDate = request.getTripStartDate();
        this.tripEndDate = request.getTripEndDate();
        this.isVotable = request.isVotable();

        return this;
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

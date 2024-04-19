package com.travelland.domain.plan;

import com.travelland.constant.PlanVoteDuration;
import com.travelland.dto.plan.PlanVoteDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long planAId; // 객체형 연관관계를 맺기엔 planId만 필요하고 planId가 바뀔일도 없음

    private Long planBId; // 객체형 연관관계를 맺기엔 planId만 필요하고 planId가 바뀔일도 없음

    private int planACount = 0;

    private int planBCount = 0;

    private Boolean isDeleted = false;

    private Boolean isClosed = false;

    @Column(length = 30)
    private String title;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private PlanVoteDuration planVoteDuration;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt; // 투표 가능기간 설정용

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    public PlanVote(PlanVoteDto.Create request) {
        this.planAId = request.getPlanAId();
        this.planBId = request.getPlanBId();
        this.title = request.getTitle();
        this.planVoteDuration = request.getPlanVoteDuration();
    }

    public PlanVote update(PlanVoteDto.Update request) {
        this.planAId = request.getPlanAId();
        this.planBId = request.getPlanBId();
        this.title = request.getTitle();
        this.planVoteDuration = request.getPlanVoteDuration();

        return this;
    }

    public void increaseAVoteCount() {
        this.planACount++;
    }
    public void increaseBVoteCount() {
        this.planBCount++;
    }
    public void decreaseAVoteCount() {
        this.planACount--;
    }
    public void decreaseBVoteCount() {
        this.planBCount--;
    }
    public void changeAtoBVoteCount() {
        this.planACount--;
        this.planBCount++;
    }
    public void changeBtoAVoteCount() {
        this.planBCount--;
        this.planACount++;
    }

    public void delete() {
        this.isDeleted = true;
    }
    public void close() {
        this.isClosed = true;
    }

    public boolean isTimeOut() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closingTime = createdAt.plus(planVoteDuration.getNumberDuration());

        if (now.isAfter(closingTime)) {
            isClosed = true;
        }

        return isClosed;
    }
}

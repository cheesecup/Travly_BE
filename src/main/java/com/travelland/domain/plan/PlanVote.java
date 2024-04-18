package com.travelland.domain.plan;

import com.travelland.dto.plan.PlanVoteDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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

    public PlanVote(PlanVoteDto.Create request) {
        this.planAId = request.getPlanAId();
        this.planBId = request.getPlanBId();
    }

    public PlanVote update(PlanVoteDto.Update request) {
        this.planAId = request.getPlanAId();
        this.planBId = request.getPlanBId();

        return this;
    }

    public void increaseAVoteCount() {
        this.planACount++;
    }

    public void increaseBVoteCount() {
        this.planBCount++;
    }

    public void delete() {
        this.isDeleted = true;
    }
}

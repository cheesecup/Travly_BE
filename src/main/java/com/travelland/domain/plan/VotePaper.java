package com.travelland.domain.plan;

import com.travelland.dto.plan.PlanVoteDto;
import com.travelland.dto.plan.VotePaperDto;
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
public class VotePaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId; // 객체형 연관관계를 맺기엔 memberId만 필요하고 memberId가 바뀔일도 없음

    private Long planVoteId; // 객체형 연관관계를 맺기엔 planVoteId만 필요하고 planVoteId가 바뀔일도 없음

    private Boolean isVotedA; // true면 A에 투표, false면 B에 투표

    private Boolean isDeleted = false;

    @Column(length = 20)
    private String content; // 투표 추가기능: 예를들어 투표사유를 적는다던가, MBTI를 적는다던가

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt; // 투표 추가기능: 일정시간 후 재투표 가능하게 할 때 사용

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    public VotePaper(VotePaperDto.Create request, Long memberId) {
        this.memberId = memberId;
        this.planVoteId = request.getPlanVoteId();
        this.isVotedA = request.getIsVotedA();
        this.content = request.getContent();
    }

    public VotePaper update(VotePaperDto.Update request) {
        this.planVoteId = request.getPlanVoteId();
        this.isVotedA = request.getIsVotedA();
        this.content = request.getContent();

        return this;
    }

    public void delete() {
        this.isDeleted = true;
    }
}

package com.travelland.domain.plan;

import com.travelland.constant.PlanVoteDuration;
import com.travelland.domain.member.Member;
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

    private Long memberId;

    @Column(length = 15)
    private String nickname;

    private String profileImage;

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

    public PlanVote(PlanVoteDto.Create request, Member member) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
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

    // 종료예정시각을 경과했는지 검사
    public boolean checkTimeOut() {
        // 이미 종료되어 있는 경우, 바로 종료상태(true) 반환
        if (isClosed == true) {
            return true;
        }

        // 아직 종료되지 않은 경우, 시간을 계산해서 종료해야하는지 체크
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closingTime = createdAt.plus(planVoteDuration.getNumberDuration());
        if (now.isAfter(closingTime)) {
            isClosed = true;
        }

        return isClosed;
    }
}

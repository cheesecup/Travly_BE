package com.travelland.dto.plan;

import com.travelland.constant.PlanVoteDuration;
import com.travelland.domain.plan.PlanVote;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public class PlanVoteDto {

    @Getter
    public static class Create {
        private PlanVoteDuration planVoteDuration;
        @Size(max = 100)
        private String title;
        private Long planAId;
        private Long planBId;
    }

    @Getter
    public static class Get {
        private final Long planVoteId;
        private final Long memberId;
        private final String nickname;
        private final String profileImage;
        private final LocalDateTime createdAt;
        private final LocalDateTime modifiedAt;
        private final String title;
        private final PlanVoteDuration planVoteDuration;
        private final Long planAId;
        private final Long planBId;
        private final String planATitle;
        private final String planBTitle;
        private final int planACount;
        private final int planBCount;
        private final Boolean isClosed;

        public Get(PlanVote planVote) {
            this.planVoteId = planVote.getId();
            this.memberId = planVote.getMember().getId();
            this.nickname = planVote.getMember().getNickname();
            this.profileImage = planVote.getMember().getProfileImage();
            this.createdAt = planVote.getCreatedAt();
            this.modifiedAt = planVote.getModifiedAt();
            this.title = planVote.getPlanVoteTitle();
            this.planVoteDuration = planVote.getPlanVoteDuration();
            this.planAId = planVote.getPlanA().getId();
            this.planBId = planVote.getPlanB().getId();
            this.planATitle = planVote.getPlanA().getTitle();
            this.planBTitle = planVote.getPlanB().getTitle();
            this.planACount = planVote.getPlanACount();
            this.planBCount = planVote.getPlanBCount();
            this.isClosed = planVote.getIsClosed();
        }
    }

    @Getter
    public static class GetAllInOne {
        private final Long planVoteId;
        private final Long memberId;
        private final String nickname;
        private final String profileImage;
        private final LocalDateTime createdAt;
        private final LocalDateTime modifiedAt;
        private final String title;
        private final PlanVoteDuration planVoteDuration;
        private final Long planAId;
        private final Long planBId;
        private final String planATitle;
        private final String planBTitle;
        private final int planACount;
        private final int planBCount;
        private final Boolean isClosed;

        public GetAllInOne(PlanVote planVote) {
            this.planVoteId = planVote.getId();
            this.memberId = planVote.getMember().getId();
            this.nickname = planVote.getMember().getNickname();
            this.profileImage = planVote.getMember().getProfileImage();
            this.createdAt = planVote.getCreatedAt();
            this.modifiedAt = planVote.getModifiedAt();
            this.title = planVote.getPlanVoteTitle();
            this.planVoteDuration = planVote.getPlanVoteDuration();
            this.planAId = planVote.getPlanA().getId();
            this.planBId = planVote.getPlanB().getId();
            this.planATitle = planVote.getPlanA().getTitle();
            this.planBTitle = planVote.getPlanB().getTitle();
            this.planACount = planVote.getPlanACount();
            this.planBCount = planVote.getPlanBCount();
            this.isClosed = planVote.getIsClosed();
        }
    }

    @Getter
    public static class Update {
        @Size(max = 100)
        private String title;
        private PlanVoteDuration planVoteDuration;
        private Long planAId;
        private Long planBId;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Close {
        private final Boolean isClosed;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Delete {
        private final Boolean isDeleted;
    }

    @Getter
    public static class Id {
        private final Long planVoteId;
        public Id(PlanVote savedPlanVote) {
            this.planVoteId = savedPlanVote.getId();
        }
    }
}

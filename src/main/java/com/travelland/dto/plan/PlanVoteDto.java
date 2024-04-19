package com.travelland.dto.plan;

import com.travelland.constant.PlanVoteDuration;
import com.travelland.domain.plan.PlanVote;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public class PlanVoteDto {

    @Getter
    public static class Create {
        //@NotBlank(message = "대상 플랜A를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planAId;
        //@NotBlank(message = "대상 플랜B를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planBId;
        private String title;
        //@NotBlank(message = "투표기간을 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private PlanVoteDuration planVoteDuration;
    }

    @Getter
    public static class Id {
        private final Long planVoteId;
        public Id(PlanVote savedPlanVote) {
            this.planVoteId = savedPlanVote.getId();
        }
    }

    @Getter
    public static class Get {
        private final Long planVoteId;
        private final Long planAId;
        private final Long planBId;
        private final int planACount;
        private final int planBCount;
        private final Boolean isClosed;
        private final String title;
        private final PlanVoteDuration planVoteDuration;
        private final LocalDateTime createdAt;
        private final LocalDateTime modifiedAt;

        public Get(PlanVote planVote) {
            this.planVoteId = planVote.getId();
            this.planAId = planVote.getPlanAId();
            this.planBId = planVote.getPlanBId();
            this.planACount = planVote.getPlanACount();
            this.planBCount = planVote.getPlanBCount();
            this.isClosed = planVote.getIsClosed();
            this.title = planVote.getTitle();
            this.planVoteDuration = planVote.getPlanVoteDuration();
            this.createdAt = planVote.getCreatedAt();
            this.modifiedAt = planVote.getModifiedAt();
        }
    }

    @Getter
    public static class Update {
        //@NotBlank(message = "대상 플랜A를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planAId;
        //@NotBlank(message = "대상 플랜B를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planBId;
        private String title;
        //@NotBlank(message = "투표기간을 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private PlanVoteDuration planVoteDuration;
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
}

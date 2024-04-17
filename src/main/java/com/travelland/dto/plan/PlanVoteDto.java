package com.travelland.dto.plan;

import com.travelland.domain.plan.PlanVote;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class PlanVoteDto {

    @Getter
    public static class Create {
        //@NotBlank(message = "대상 플랜A를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planAId;
        //@NotBlank(message = "대상 플랜B를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planBId;
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
        private final Long planAId;
        private final Long planBId;
        private final int planACount;
        private final int planBCount;

        public Get(PlanVote planVote) {
            this.planAId = planVote.getPlanAId();
            this.planBId = planVote.getPlanBId();
            this.planACount = planVote.getPlanACount();
            this.planBCount = planVote.getPlanBCount();
        }
    }

    @Getter
    public static class Update {
        //@NotBlank(message = "대상 플랜A를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planAId;
        //@NotBlank(message = "대상 플랜B를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planBId;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Delete {
        private final Boolean isDeleted;
    }
}

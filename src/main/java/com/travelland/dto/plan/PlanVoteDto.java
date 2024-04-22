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
        //@NotBlank(message = "대상 플랜A를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planAId;
        //@NotBlank(message = "대상 플랜B를 선택해주세요.", groups = PlanValidationGroups.VoteBlankGroup.class)
        private Long planBId;
        @Size(max = 100)
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
        private final String title;
        private final LocalDateTime createdAt;
        private final LocalDateTime modifiedAt;
        private final PlanVoteDuration planVoteDuration;
        private final Boolean isClosed;
        private final Long memberId;
        private final String nickname;
        private final String profileImage;
        private final Long planAId;
        private final Long planBId;
        private final String planATitle;
        private final String planBTitle;
        private final int planACount;
        private final int planBCount;

        public Get(PlanVote planVote) {
            this.planVoteId = planVote.getId();
            this.title = planVote.getPlanVoteTitle();
            this.createdAt = planVote.getCreatedAt();
            this.modifiedAt = planVote.getModifiedAt();
            this.isClosed = planVote.getIsClosed();
            this.planVoteDuration = planVote.getPlanVoteDuration();
            this.memberId = planVote.getMember().getId();
            this.nickname = planVote.getMember().getNickname();
            this.profileImage = planVote.getMember().getProfileImage();
            this.planAId = planVote.getPlanA().getId();
            this.planBId = planVote.getPlanB().getId();
            this.planATitle = planVote.getPlanA().getTitle();
            this.planBTitle = planVote.getPlanB().getTitle();
            this.planACount = planVote.getPlanACount();
            this.planBCount = planVote.getPlanBCount();
        }
    }

    @Getter
    public static class GetAllInOne {
        private final Long planVoteId;
        private final String title;
        private final LocalDateTime createdAt;
        private final LocalDateTime modifiedAt;
        private final PlanVoteDuration planVoteDuration;
        private final Boolean isClosed;
        private final Long memberId;
        private final String nickname;
        private final String profileImage;
        private final Long planAId;
        private final Long planBId;
        private final String planATitle;
        private final String planBTitle;
        private final int planACount;
        private final int planBCount;

        public GetAllInOne(PlanVote planVote) {
            this.planVoteId = planVote.getId();
            this.title = planVote.getPlanVoteTitle();
            this.createdAt = planVote.getCreatedAt();
            this.modifiedAt = planVote.getModifiedAt();
            this.isClosed = planVote.getIsClosed();
            this.planVoteDuration = planVote.getPlanVoteDuration();
            this.memberId = planVote.getMember().getId();
            this.nickname = planVote.getMember().getNickname();
            this.profileImage = planVote.getMember().getProfileImage();
            this.planAId = planVote.getPlanA().getId();
            this.planBId = planVote.getPlanB().getId();
            this.planATitle = planVote.getPlanA().getTitle();
            this.planBTitle = planVote.getPlanB().getTitle();
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
        @Size(max = 100)
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

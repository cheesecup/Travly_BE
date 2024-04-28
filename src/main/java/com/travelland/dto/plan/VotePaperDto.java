package com.travelland.dto.plan;

import com.travelland.domain.plan.VotePaper;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public class VotePaperDto {

    @Getter
    public static class Create {
        private Long planVoteId;
        private Boolean isVotedA;
        @Size(max = 20)
        private String content;
    }

    @Getter
    public static class Get {
        private final Long votePaperId;
        private final Long memberId;
        private final Long planVoteId;
        private final Boolean isVotedA;
        private final String content;
        private final LocalDateTime createdAt;
        private final LocalDateTime modifiedAt;

        public Get(VotePaper votePaper) {
            this.votePaperId = votePaper.getId();
            this.memberId = votePaper.getMemberId();
            this.planVoteId = votePaper.getPlanVoteId();
            this.isVotedA = votePaper.getIsVotedA();
            this.content = votePaper.getContent();
            this.createdAt = votePaper.getCreatedAt();
            this.modifiedAt = votePaper.getModifiedAt();
        }
    }

    @Getter
    public static class Update {
        private Long planVoteId;
        private Boolean isVotedA;
        @Size(max = 20)
        private String content;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Delete {
        private final Boolean isDeleted;
    }

    @Getter
    public static class Id {
        private final Long votePaperId;
        public Id(VotePaper savedVotePaper) {
            this.votePaperId = savedVotePaper.getId();
        }
    }
}

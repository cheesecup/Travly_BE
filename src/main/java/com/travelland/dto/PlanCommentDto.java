package com.travelland.dto;

import com.travelland.domain.plan.PlanComment;
import lombok.Getter;

import java.time.LocalDateTime;

public class PlanCommentDto {

    @Getter
    public static class Create {
        private String content;
    }

    @Getter
    public static class Id {
        private final Long commentId;
        public Id(PlanComment savedPlanComment) {
            this.commentId = savedPlanComment.getId();
        }
    }

    @Getter
    public static class Get {
        private final Long commentId;
        private final String content;
        private final LocalDateTime createdAt;
        private final LocalDateTime modifiedAt;
        private final String memberNickname;

        public Get(PlanComment planComment) {
            this.commentId = planComment.getId();
            this.content = planComment.getContent();
            this.createdAt = planComment.getCreatedAt();
            this.modifiedAt = planComment.getModifiedAt();
            this.memberNickname = planComment.getMember().getNickname();
        }
    }

    @Getter
    public static class Update {
        private String content;
    }

    @Getter
    public static class Delete {
        private final Boolean isDeleted;
        public Delete(Boolean result) {
            this.isDeleted = result;
        }
    }
}

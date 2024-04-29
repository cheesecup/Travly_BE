package com.travelland.dto.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.domain.plan.Plan;
import com.travelland.valid.plan.PlanValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PlanDto {

    @Getter
    public static class CreateAllInOne {
        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;
        @NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String area;
        private int budget;
        private Boolean isPublic;
        private Boolean isVotable;
        private List<DayPlanDto.CreateAllInOne> dayPlans;
    }

    @Getter
    public static class Get {
        private final Long planId;
        private final String title;
        private final LocalDate tripStartDate;
        private final LocalDate tripEndDate;
        private final String area;
        private final int budget;
        private final Boolean isPublic;
        private final Boolean isVotable;
        private final String memberNickname;
        private final LocalDateTime createdAt;
        private final int viewCount;
        private final int likeCount;

        public Get(Plan plan) {
            this.planId = plan.getId();
            this.title = plan.getTitle();
            this.tripStartDate = plan.getTripStartDate();
            this.tripEndDate = plan.getTripEndDate();
            this.area = plan.getArea();
            this.budget = plan.getBudget();
            this.isPublic = plan.getIsPublic();
            this.isVotable = plan.getIsVotable();
            this.memberNickname = plan.getMember().getNickname();
            this.createdAt = plan.getCreatedAt();
            this.viewCount = plan.getViewCount();
            this.likeCount = plan.getLikeCount();
        }
    }

    @Getter
    public static class GetAllInOne {
        private final Long planId;
        private final String title;
        private final LocalDate tripStartDate;
        private final LocalDate tripEndDate;
        private final String area;
        private final int budget;
        private final Boolean isPublic;
        private final Boolean isVotable;
        private final String memberNickname;
        private final LocalDateTime createdAt;
        private final int viewCount;
        private final int likeCount;
        private final String profileUrl;
        private final Boolean isWriter;
        private final Boolean isLike;
        private final Boolean isScrap;
        private List<DayPlanDto.GetAllInOne> dayPlans;
        private List<PlanVoteDto.GetAllInOne> planVotes;

        @Builder
        public GetAllInOne(Plan plan, List<DayPlanDto.GetAllInOne> dayPlans, List<PlanVoteDto.GetAllInOne> planVotes, Boolean isLike, Boolean isScrap, Boolean isWriter) {
            this.planId = plan.getId();
            this.title = plan.getTitle();
            this.tripStartDate = plan.getTripStartDate();
            this.tripEndDate = plan.getTripEndDate();
            this.area = plan.getArea();
            this.budget = plan.getBudget();
            this.isPublic = plan.getIsPublic();
            this.isVotable = plan.getIsVotable();
            this.memberNickname = plan.getMember().getNickname();
            this.createdAt = plan.getCreatedAt();
            this.viewCount = plan.getViewCount();
            this.likeCount = plan.getLikeCount();
            this.profileUrl = plan.getMember().getProfileImage();
            this.isWriter = isWriter;
            this.isLike = isLike;
            this.isScrap = isScrap;
            this.dayPlans = dayPlans;
            this.planVotes = planVotes;
        }
    }

    @Getter
    public static class UpdateAllInOne {
        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;
        @NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String area;
        private int budget;
        private Boolean isPublic;
        private Boolean isVotable;
        private List<DayPlanDto.UpdateAllInOne> dayPlans;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Delete {
        private final Boolean isDeleted;
    }

    @Getter
    public static class Id {
        private final Long planId;
        public Id(Plan savedPlan) {
            this.planId = savedPlan.getId();
        }
    }

    @Getter
    public static class Invitee {
        private String nickname;
        private String email;
    }
}

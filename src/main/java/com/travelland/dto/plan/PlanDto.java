package com.travelland.dto.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.valid.plan.PlanValidationGroups;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanLike;
import com.travelland.domain.plan.PlanScrap;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PlanDto {

    @Getter
    @AllArgsConstructor
    public static class Create {
        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
//        @NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
//        @Size(max = 1000)
//        private String content;
        private int budget;
        @NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String area;
        private Boolean isPublic;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;
        private Boolean isVotable;
    }

    @Getter
    public static class CreateAllInOne {
        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
//        @NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
//        @Size(max = 1000)
//        private String content;
        private int budget;
        @NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String area;
        private Boolean isPublic;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;
        private Boolean isVotable;
        private List<DayPlanDto.CreateAllInOne> dayPlans;
    }

    @Getter
    public static class Id {
        private final Long planId;
        public Id(Plan savedPlan) {
            this.planId = savedPlan.getId();
        }
    }

    @Getter
    public static class Get {
        private final Long planId;
        private final String title;
//        private final String content;
        private final int budget;
        private final String area;
        private final Boolean isPublic;
        private final LocalDate tripStartDate;
        private final LocalDate tripEndDate;
        private final int viewCount;
        private final int likeCount;
        private final Boolean isVotable;
        private final LocalDateTime createdAt;
        private final String memberNickname;

        public Get(Plan plan) {
            this.planId = plan.getId();
            this.title = plan.getTitle();
//            this.content = plan.getContent();
            this.budget = plan.getBudget();
            this.area = plan.getArea();
            this.isPublic = plan.getIsPublic();
            this.tripStartDate = plan.getTripStartDate();
            this.tripEndDate = plan.getTripEndDate();
            this.viewCount = plan.getViewCount();
            this.likeCount = plan.getLikeCount();
            this.isVotable = plan.getIsVotable();
            this.createdAt = plan.getCreatedAt();
            this.memberNickname = plan.getMember().getNickname();
        }
    }

    @Getter
    public static class GetAllInOne {
        private final Long planId;
        private final String title;
//        private final String content;
        private final int budget;
        private final String area;
        private final Boolean isPublic;
        private final LocalDate tripStartDate;
        private final LocalDate tripEndDate;
        private final int viewCount;
        private final int likeCount;
        private final Boolean isVotable;
        private final LocalDateTime createdAt;
        private final String memberNickname;
        private final String profileUrl;
        private List<DayPlanDto.GetAllInOne> dayPlans;
        private List<PlanVoteDto.GetAllInOne> planVotes;
        private final Boolean isLike;
        private final Boolean isScrap;
        private final Boolean isWriter;

        @Builder
        public GetAllInOne(Plan plan, List<DayPlanDto.GetAllInOne> dayPlans, List<PlanVoteDto.GetAllInOne> planVotes, Boolean isLike, Boolean isScrap, Boolean isWriter) {
            this.planId = plan.getId();
            this.title = plan.getTitle();
//            this.content = plan.getContent();
            this.budget = plan.getBudget();
            this.area = plan.getArea();
            this.isPublic = plan.getIsPublic();
            this.tripStartDate = plan.getTripStartDate();
            this.tripEndDate = plan.getTripEndDate();
            this.viewCount = plan.getViewCount();
            this.likeCount = plan.getLikeCount();
            this.isVotable = plan.getIsVotable();
            this.createdAt = plan.getCreatedAt();
            this.memberNickname = plan.getMember().getNickname();
            this.profileUrl = plan.getMember().getProfileImage();
            this.dayPlans = dayPlans;
            this.planVotes = planVotes;
            this.isLike = isLike;
            this.isScrap = isScrap;
            this.isWriter = isWriter;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class GetList {
        private final Long planId;
        private final String title;
        private final int viewCount;
        private final LocalDateTime createdAt;

        public GetList(Plan plan) {
            this.planId = plan.getId();
            this.title = plan.getTitle();
            this.viewCount = plan.getViewCount();
            this.createdAt = plan.getCreatedAt();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class GetLists {
        private List<PlanDto.GetList> listList;
        private Long totalCount;
    }

    @Getter
    @AllArgsConstructor
    public static class Update {
        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
//        @NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
//        @Size(max = 1000)
//        private String content;
        private int budget;
        @NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String area;
        private Boolean isPublic;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;
        private Boolean isVotable;
    }

    @Getter
    public static class UpdateAllInOne {
        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
//        @NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
//        @Size(max = 1000)
//        private String content;
        private int budget;
        @NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String area;
        private Boolean isPublic;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;
        private Boolean isVotable;
        private List<DayPlanDto.UpdateAllInOne> dayPlans;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Delete {
        private final Boolean isDeleted;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Result {
        private final Boolean result;
    }

    @Getter
    @AllArgsConstructor
    public static class Likes {
        private Long planId;
        private String title;
        private String nickname;
        private LocalDate tripStartDate;
        private LocalDate tripEndDate;

        public Likes(PlanLike planLike){
            this.planId = planLike.getPlan().getId();
            this.title = planLike.getPlan().getTitle();
            this.nickname = planLike.getMember().getNickname();
            this.tripStartDate = planLike.getPlan().getTripStartDate();
            this.tripEndDate = planLike.getPlan().getTripEndDate();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Scraps {
        private Long planId;
        private String title;
        private String nickname;
        private LocalDate tripStartDate;
        private LocalDate tripEndDate;

        public Scraps(PlanScrap planScrap){
            this.planId = planScrap.getPlan().getId();
            this.title = planScrap.getPlan().getTitle();
            this.nickname = planScrap.getMember().getNickname();
            this.tripStartDate = planScrap.getPlan().getTripStartDate();
            this.tripEndDate = planScrap.getPlan().getTripEndDate();
        }
    }
}

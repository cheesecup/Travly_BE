package com.travelland.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TripCommentDto {

    @Getter
    public static class Create {
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class Id {
        private Long tripCommentId;
    }

    @Getter
    @AllArgsConstructor
    public static class GetList {
        private String content;
        private String nickname;
        private String thumbnailProfileImage;

    }

    @Getter
    public static class Update {
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class Delete {
        private Boolean isDeleted;

    }
}

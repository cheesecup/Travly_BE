package com.travelland.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TripCommentDto {

    /**
     * 등록할 댓글 내용을 담는 DTO
     */
    @Getter
    public static class Create {
        private String content;
    }

    /**
     * 댓글 id를 담는 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class Id {
        private Long tripCommentId;
    }

    /**
     * 댓글 목록을 담는 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class GetList {
        private String content;
        private String nickname;
        private String thumbnailProfileImage;
    }

    /**
     * 수정한 댓글 내용을 담는 DTO
     */
    @Getter
    public static class Update {
        private String content;
    }

    /**
     * 댓글 삭제 결과를 담는 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class Delete {
        private Boolean isDeleted;

    }
}

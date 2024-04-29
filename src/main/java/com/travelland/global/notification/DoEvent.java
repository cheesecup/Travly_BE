package com.travelland.global.notification;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DoEvent {

    @Getter
    public static class DoInviteEvent extends ApplicationEvent {
        private final Long planId;
        private final String invitee;

        public DoInviteEvent(Object source, Long planId, String invitee) {
            super(source);
            this.planId = planId;
            this.invitee = invitee;
        }
    }

    @Getter
    public static class DoAgreeEvent extends ApplicationEvent {
        private final Long planId;
        private final String inviteeNickname;

        public DoAgreeEvent(Object source, Long planId, String invitee) {
            super(source);
            this.planId = planId;
            this.inviteeNickname = invitee;
        }
    }

    @Getter
    public static class DoDisagreeEvent extends ApplicationEvent {
        private final Long planId;
        private final String inviteeNickname;

        public DoDisagreeEvent(Object source, Long planId, String invitee) {
            super(source);
            this.planId = planId;
            this.inviteeNickname = invitee;
        }
    }

    @Getter
    public static class DoVoteEvent extends ApplicationEvent {
        private final Long planId;
        private final String invitee;

        public DoVoteEvent(Object source, Long planId, String invitee) {
            super(source);
            this.planId = planId;
            this.invitee = invitee;
        }
    }
}

package com.travelland.global.notify;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class DoEvent {

    @Getter
    public static class DoInviteEvent extends ApplicationEvent {
        private final Long planId;
        private final List<String> invitee;
        private final String invitor;

        public DoInviteEvent(Object source, Long planId, List<String> invitee, String invitor) {
            super(source);
            this.planId = planId;
            this.invitee = invitee;
            this.invitor = invitor;
        }
    }

    @Getter
    public static class DoAgreeEvent extends ApplicationEvent {
        private final Long planId;
        private final String invitee;

        public DoAgreeEvent(Object source, Long planId, String invitee) {
            super(source);
            this.planId = planId;
            this.invitee = invitee;
        }
    }

    @Getter
    public static class DoDisagreeEvent extends ApplicationEvent {
        private final Long planId;
        private final String invitee;

        public DoDisagreeEvent(Object source, Long planId, String invitee) {
            super(source);
            this.planId = planId;
            this.invitee = invitee;
        }
    }
}

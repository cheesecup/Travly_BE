package com.travelland.constant;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum PlanVoteDuration {
    ONE_MINUTE("ONE_MINUTE"),
    ONE_SECOND("ONE_SECOND"),
    HALF_DAY("HALF_DAY"),
    ONE_DAY("ONE_DAY"),
    THREE_DAY("THREE_DAY"),
    SEVEN_DAY("SEVEN_DAY");

    private final String planVoteDuration;
    private final Duration numberDuration;

    PlanVoteDuration(String planVoteDuration) {
        this.planVoteDuration = planVoteDuration;
        this.numberDuration = getNumberDuration(planVoteDuration);
    }

    private Duration getNumberDuration(String planVoteDuration) {
        return switch (planVoteDuration) {
            case "ONE_MINUTE" -> Duration.ofMinutes(1);
            case "ONE_SECOND" -> Duration.ofSeconds(1);
            case "HALF_DAY" -> Duration.ofHours(12);
            case "ONE_DAY" -> Duration.ofDays(1);
            case "THREE_DAY" -> Duration.ofDays(3);
            case "SEVEN_DAY" -> Duration.ofDays(7);
            default -> throw new IllegalArgumentException("Invalid duration: " + planVoteDuration);
        };
    }
}
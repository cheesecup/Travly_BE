package com.travelland.domain.plan;

import com.travelland.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private Boolean isDeleted;

    public PlanScrap(Member member, Plan plan) {
        this.member = member;
        this.plan = plan;
        this.isDeleted = false;
    }

    public void registerScrap() {
        this.isDeleted = false;
    }

    public void cancelScrap() {
        this.isDeleted = true;
    }
}

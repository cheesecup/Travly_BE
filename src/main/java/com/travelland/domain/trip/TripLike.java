package com.travelland.domain.trip;

import com.travelland.domain.Member;
import com.travelland.domain.trip.Trip;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    private boolean isDeleted;

    public TripLike(Member member, Trip trip) {
        this.member = member;
        this.trip = trip;
        this.isDeleted = false;
    }

    public void registerLike() {
        this.isDeleted = false;
    }

    public void cancelLike() {
        this.isDeleted = true;
    }
}

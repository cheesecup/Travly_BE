package com.travelland.domain.trip;

import com.travelland.domain.trip.Trip;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    public TripHashtag(String title, Trip trip) {
        this.title = title;
        this.trip = trip;
    }

    public void update(String title) {
        this.title = title;
    }
}

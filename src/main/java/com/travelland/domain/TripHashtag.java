package com.travelland.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class TripHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    protected TripHashtag() {}

    public TripHashtag(String title, Trip trip) {
        this.title = title;
        this.trip = trip;
    }

    public void update(String title) {
        this.title = title;
    }
}

package com.travelland.domain;

import com.travelland.dto.TripImageDto.CreateRequest;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class TripImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private String storeImageName;

    private boolean isThumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    protected TripImage() {}

    public TripImage(CreateRequest requestDto, boolean isThumbnail, Trip trip) {
        this.imageUrl = requestDto.getImageUrl();
        this.storeImageName = requestDto.getStoreImageName();
        this.isThumbnail = isThumbnail;
        this.trip = trip;
    }
}

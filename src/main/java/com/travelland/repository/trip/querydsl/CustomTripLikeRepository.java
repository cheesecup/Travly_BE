package com.travelland.repository.trip.querydsl;

import com.travelland.domain.Member;
import com.travelland.domain.trip.TripLike;

import java.util.List;

public interface CustomTripLikeRepository {
    List<TripLike> getLikeListByMember(Member member, int size, int page);
}

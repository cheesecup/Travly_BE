package com.travelland.repository.trip.querydsl;

import com.travelland.domain.member.Member;
import com.travelland.domain.TripLike;

import java.util.List;

public interface CustomTripLikeRepository {
    List<TripLike> getLikeListByMember(Member member, int size, int page);
}

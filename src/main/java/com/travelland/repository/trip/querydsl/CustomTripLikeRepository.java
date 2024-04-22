package com.travelland.repository.trip.querydsl;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripLike;

import java.util.List;

public interface CustomTripLikeRepository {
    List<TripLike> getLikeListByMember(Member member, int size, int page);

    List<Long> getMemberIdsByTripId(Long tripId, int size, int page);

    List<Long> getTripIdsByMemberId(Long memberId, int size, int page);

    long deleteByTrip(Trip trip);
}

package com.travelland.repository.trip.querydsl;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.TripScrap;

import java.util.List;

public interface CustomTripScrapRepository {

    List<TripScrap> getScrapListByMember(Member member, int size, int page);
}

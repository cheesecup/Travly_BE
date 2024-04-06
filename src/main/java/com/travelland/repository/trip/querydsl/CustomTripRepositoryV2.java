package com.travelland.repository.trip.querydsl;

import com.travelland.domain.Member;
import com.travelland.domain.trip.Trip;

import java.util.List;

public interface CustomTripRepositoryV2 {

    List<Trip> getTripList(int page, int size, String sort, boolean ASC);

    List<Trip> getMyTripList(int page, int size, Member member);

    List<Trip> searchTripByHashtag(String hashtag, int page, int size, String sort, boolean ASC);
}

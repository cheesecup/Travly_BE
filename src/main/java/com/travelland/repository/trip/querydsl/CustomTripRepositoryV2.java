package com.travelland.repository.trip.querydsl;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.global.job.DataSet;

import java.util.List;
import java.util.Optional;

public interface CustomTripRepositoryV2 {
    List<Trip> getTripList(int page, int size, String sort, boolean ASC);

    List<Trip> getMyTripList(int page, int size, Member member);

    List<Trip> searchTripByHashtag(String hashtag, int page, int size, String sort, boolean ASC);

    void updateBulkViewCount(List<DataSet> dataSets);

    Optional<Trip> getTripById(Long tripId);

}

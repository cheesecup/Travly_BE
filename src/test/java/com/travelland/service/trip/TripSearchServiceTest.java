package com.travelland.service.trip;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest

class TripSearchServiceTest {
    @Autowired
    private TripSearchService tripSearchService;

    @Autowired
    private TripService tripService;

    @Test
    void getTripList() {
        System.out.println("tripSearchService = " + tripSearchService.getTripList(1,20,"createdAt",true));
    }
    @Test
    void getTripByKeyword(){
        System.out.println("tripSearchService = " + tripSearchService.searchTripByField("hashtag", "여행",1,20,"createdAt",true).getTotalCount());
    }
    @Test
    void getRankByViewCount(){
        System.out.println("tripSearchService = " + tripService.getRankByViewCount(10L));
    }
}
package com.travelland.service.trip;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
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
        System.out.println("tripSearchService = " + tripSearchService.searchTripByHashtag("여행",1,20,"createdAt",true).getTotalCount());
    }
    @Test
    void getRank(){
        System.out.println("tripSearchService = " + tripSearchService.getRecentlyTopSearch().get(0).getStatus());
    }
    @Test
    void getRankByViewCount(){
        System.out.println("tripSearchService = " + tripService.getRankByViewCount(10L));
    }
}
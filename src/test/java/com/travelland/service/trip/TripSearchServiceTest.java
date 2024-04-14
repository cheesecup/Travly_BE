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

    @Test
    void getTripList() {
        System.out.println("tripSearchService = " + tripSearchService.getTripList(1,20,"createdAt",true));
    }
    @Test
    void getTripByKeyword(){
        System.out.println("tripSearchService = " + tripSearchService.searchTripByHashtag("여행",1,20,"createdAt",true).getTotalCount());
    }
    @Test
    void getRank() throws IOException {
        System.out.println("tripSearchService = " + tripSearchService.getRecentlyTopSearch().get(0).getStatus());
    }
}
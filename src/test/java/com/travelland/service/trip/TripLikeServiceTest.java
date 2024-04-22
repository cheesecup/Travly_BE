package com.travelland.service.trip;

import com.travelland.service.plan.PlanLikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
class TripLikeServiceTest {
    @Autowired
    private PlanLikeService planLikeService;
    @Autowired
    private TripLikeService tripLikeService;

//    @Test
//    void increase() {
//        for(int i = 0 ; i < 100000;i++){
//            planLikeService.registerPlanLike(1L,"a"+i);
//        }
//    }
//
//    @Test
//    void getPlanLikeCount() {
//        System.out.println("planLikeService = " + planLikeService.getPlanLikeCount(1L));
//    }
//
//    @Test
//    void getPlanLikeCountRedis() {
//        System.out.println("planLikeService = " + planLikeService.getPlanLikeCountRedis(1L));
//
//    }


//    @Test
//    void createLike() {
//        for(int i = 0 ; i < 100000 ; i++){
//            Random random = new Random();
//            tripLikeService.registerTripLike(random.nextLong(8000L)+20,"a10"+random.nextLong(6000L));
//        }
//    }
}
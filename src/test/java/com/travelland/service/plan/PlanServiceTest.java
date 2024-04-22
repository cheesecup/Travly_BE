//package com.travelland.service.plan;
//
//import com.travelland.dto.plan.PlanDto;
//import jakarta.transaction.Transactional;
//import net.datafaker.Faker;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//
//import java.time.LocalDate;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.IntStream;
//
//@SpringBootTest
//class PlanServiceTest {
//    @Autowired
//    private PlanService planService;
//
//    // Java Faker를 사용하여 Create 객체를 랜덤하게 생성하는 메서드
//    public static PlanDto.Create generateRandomCreate(Faker faker) {
//        // 각 필드를 랜덤한 값으로 채워서 Create 객체 생성
//        String title = faker.lorem().sentence();
//        String content = faker.lorem().sentence();
//        int budget = faker.number().numberBetween(10000, 1000000);
//        String area = faker.address().city();
//        boolean isPublic = faker.random().nextBoolean();
//        LocalDate tripStartDate = LocalDate.parse(faker.date().past(1, TimeUnit.DAYS, "yyyy-MM-dd"));
//        LocalDate tripEndDate = LocalDate.parse(faker.date().future(1, TimeUnit.DAYS, "yyyy-MM-dd"));
//        boolean isVotable = faker.random().nextBoolean();
//
//        return new PlanDto.Create(title, content, budget, area, isPublic, tripStartDate, tripEndDate, isVotable);
//    }
//
//
//    @Test
//    void createPlan() {
//        Faker faker = new Faker();
//        planService.createPlan(generateRandomCreate(faker), "test@test.com");
//    }
//
//    @Test
//    void readPlanList() {
//        Faker faker = new Faker();
//        int COUNT_UNIT = 100000;
//        int n = 0;
//        IntStream.range(COUNT_UNIT * n, COUNT_UNIT * (n+1))
//                    .forEach(i -> planService.createPlan(generateRandomCreate(faker), "test@test.com"));
//        planService.readPlanListRedis(5, 20, "createdAt", false);
//    }
//}
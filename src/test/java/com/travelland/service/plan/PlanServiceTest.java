package com.travelland.service.plan;

import com.travelland.dto.PlanDto;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Transactional
class PlanServiceTest {
    @Autowired
    private PlanService planService;

    // Java Faker를 사용하여 Create 객체를 랜덤하게 생성하는 메서드
    public static PlanDto.Create generateRandomCreate(Faker faker) {
        // 각 필드를 랜덤한 값으로 채워서 Create 객체 생성
        String title = faker.lorem().sentence();
        String content = faker.lorem().paragraph();
        int budget = faker.number().numberBetween(10000, 1000000);
        String area = faker.address().city();
        boolean isPublic = faker.random().nextBoolean();
        LocalDate tripStartDate = LocalDate.parse(faker.date().past(1, TimeUnit.DAYS, "yyyy-MM-dd"));
        LocalDate tripEndDate = LocalDate.parse(faker.date().future(1, TimeUnit.DAYS, "yyyy-MM-dd"));
        boolean isVotable = faker.random().nextBoolean();

        return new PlanDto.Create(title, content, budget, area, isPublic, tripStartDate, tripEndDate, isVotable);
    }


    @Test
    void createPlan() {
        Faker faker = new Faker();
        planService.createPlan(generateRandomCreate(faker), "test@test.com");
    }

    @Test
    void readPlanList() {
        Faker faker = new Faker();
        int COUNT_UNIT = 100_000;
        planService.createPlan(generateRandomCreate(faker), "test@test.com");
        int n = 0;
//        IntStream.range(COUNT_UNIT * n, COUNT_UNIT * (n+1))
//                    .forEach(i -> planService.createPlan(generateRandomCreate(faker), "test@test.com"));

        Page<PlanDto.Read> plans = planService.readPlanList(1,10,"createdAt",true);

        // 페이지 정보 출력
        System.out.println("Total elements: " + plans.getTotalElements());
        System.out.println("Total pages: " + plans.getTotalPages());
        System.out.println("Current page number: " + plans.getNumber());
        System.out.println("Number of elements in current page: " + plans.getNumberOfElements());
        System.out.println("Page size: " + plans.getSize());

        // 페이지에 포함된 요소 출력
        plans.getContent().forEach(System.out::println);

    }

    @Test
    void readPlanById() {
    }

    @Test
    void updatePlan() {
    }

    @Test
    void deletePlan() {
    }

    @Test
    void createDayPlan() {
    }

    @Test
    void readDayPlan() {
    }

    @Test
    void updateDayPlan() {
    }

    @Test
    void deleteDayPlan() {
    }

    @Test
    void createUnitPlan() {
    }

    @Test
    void readUnitPlan() {
    }

    @Test
    void updateUnitPlan() {
    }

    @Test
    void deleteUnitPlan() {
    }
}
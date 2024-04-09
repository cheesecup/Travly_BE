package com.travelland.service.trip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TripServiceTest {

    @Autowired
    private TripService tripService;

//    public static TripDto.Create generateRandomCreate(Faker faker, List<String> hashtag) {
//        Collections.shuffle(hashtag);
//        List<String> hashtagList = hashtag.subList(0, 2);
//
//        // 각 필드를 랜덤한 값으로 채워서 Create 객체 생성
//        String title = faker.lorem().sentence(); // 랜덤한 제목 생성
//        String content = faker.lorem().sentence(); // 랜덤한 내용 생성
//        int cost = faker.number().numberBetween(10000, 1000000);
//        String address = faker.address().fullAddress();
//        String placeName = faker.lorem().word(); // 임의의 장소 이름
//        String x = faker.address().latitude(); // 임의의 x 좌표
//        String y= faker.address().longitude(); // 임의의 y 좌표
//        boolean isPublic = faker.random().nextBoolean();
//        LocalDate tripStartDate = LocalDate.parse(faker.date().past(1, TimeUnit.DAYS, "yyyy-MM-dd"));
//        LocalDate tripEndDate = LocalDate.parse(faker.date().future(1, TimeUnit.DAYS, "yyyy-MM-dd"));
//
//
//        return new TripDto.Create(title, content, tripStartDate, tripEndDate, cost, hashtagList, address, placeName, x, y, isPublic);
//    }
//
//    private static List<String> generateHashTags() {
//        List<String> hashTags = new ArrayList<>();
//        hashTags.add("분위기");
//        hashTags.add("낭만");
//        hashTags.add("액티비티");
//        hashTags.add("힐링");
//        hashTags.add("2030");
//
//        return hashTags;
//    }

//    @Test
//    void createTrip() {
//        Faker faker = new Faker();
//        List<String> hashtag = generateHashTags();
//
//        for (int i = 0; i < 100000; i++) {
//            tripService.createTrip(generateRandomCreate(faker, hashtag), null, "test@test.com");
//        }
//    }

}
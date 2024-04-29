package com.travelland.service.trip;

import com.travelland.constant.Role;
import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripRecommendDoc;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripRecommendRepository;
import com.travelland.repository.trip.TripRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE,
        connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class TripSearchServiceTest {
    @Autowired
    TripSearchService tripSearchService;
    @Autowired
    TripRecommendRepository tripRecommendRepository;
    @Autowired
    TripRepository tripRepository;
    @Autowired
    MemberRepository memberRepository;
    private final List<Trip> trips = new ArrayList<>();

    @BeforeAll
    void createData(){
        List<String> hashtag = new ArrayList<>();
        hashtag.add("힐링");
        hashtag.add("추억");
        hashtag.add("여행");

        for(int i = 0 ; i < 2 ; i++){
            Member member = new Member(String.format("test%d@test.com", i),
                    "1234",String.format("TEST%d", i),Role.USER,"imgUrl");

            Member savedMember = memberRepository.save(member);

            TripDto.Create requestDto = new TripDto.Create(
                    String.format("여행TEST%d", i),"여행관련 내용", LocalDate.now(), LocalDate.now().plusDays(1L),
                    5000, hashtag,"서울시 강남구","강남역",true);

            Trip trip = tripRepository.save(new Trip(requestDto, savedMember));
            this.trips.add(trip);

            tripSearchService.createTripDocument(trip, hashtag,"thumbnailUrl");
        }
    }

    @AfterAll
    void deleteData(){
        this.trips.forEach(trip -> tripSearchService.deleteTrip(trip.getId()));
    }

    @Test
    @DisplayName("통합 검색 기능 TEST")
    void totalSearchTrip() {
        TripDto.SearchResult result =
                tripSearchService.totalSearchTrip("ㅅㅇ",1,8,"createdAt",false);
        Assertions.assertEquals("ㅅㅇ", result.getResultKeyword());
        Assertions.assertNotNull(result.getSearches());
        Assertions.assertNotEquals(0, result.getTotalCount());
        Assertions.assertFalse(result.getSearches().isEmpty());
    }

    @Test
    @DisplayName("제목 검색 기능 TEST")
    void searchTripByTitle() {
        TripDto.SearchResult result =
                tripSearchService.searchTripByTitle("여행TEST",1,5,"createdAt",false);

        Assertions.assertEquals("여행TEST", result.getResultKeyword());
        Assertions.assertNotNull(result.getSearches());
        Assertions.assertEquals("강남역", result.getSearches().get(0).getPlaceName());
        Assertions.assertNotEquals(0, result.getTotalCount());
        Assertions.assertFalse(result.getSearches().isEmpty());
    }

    @Test
    @Order(0)
    @DisplayName("field명 검색 기능 TEST")
    void searchTripByField() {
        TripDto.SearchResult result =
                tripSearchService.searchTripByField("hashtag","여행",1,5,"createdAt",false);

        Assertions.assertEquals("여행", result.getResultKeyword());
        Assertions.assertNotNull(result.getSearches());
        Assertions.assertEquals("강남역", result.getSearches().get(0).getPlaceName());
        Assertions.assertNotEquals(0, result.getTotalCount());
        Assertions.assertFalse(result.getSearches().isEmpty());
    }

    @Test
    @DisplayName("지역 검색 기능 TEST")
    void searchTripByArea() {
        TripDto.SearchResult result =
                tripSearchService.searchTripByArea("서울시",1,5,"createdAt",false);

        Assertions.assertEquals("서울시", result.getResultKeyword());
        Assertions.assertNotNull(result.getSearches());
        Assertions.assertEquals("강남역", result.getSearches().get(0).getPlaceName());
        Assertions.assertNotEquals(0, result.getTotalCount());
        Assertions.assertFalse(result.getSearches().isEmpty());
    }

    @Test
    @DisplayName("키워드 없는 검색")
    void getTripList() {
        List<TripDto.GetList> result =
                tripSearchService.getTripList(1,5,"createdAt",false);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName(" tripId list 조회 TEST")
    void getRankByViewCount() {
        List<Long> keys = this.trips.stream().map(Trip::getId).toList();
        List<TripDto.Top10> result =
                tripSearchService.getRankByViewCount(keys);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("강남역", result.get(0).getPlaceName());
    }

    @Test
    @Order(1)
    @DisplayName(" 인기 검색어 조회 TEST")
    void getRecentlyTopSearch() {
        List<String> result = tripSearchService.getRecentlyTopSearch("hashtag");
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName(" 내가 쓴 여행글 List 조회 TEST")
    void getMyTripList() {
        List<TripDto.GetList> result = tripSearchService.getMyTripList(1,10,"test1@test.com");
        Assertions.assertNotNull(result.get(0).getTripId());
    }

    @Test
    @DisplayName(" random 글 조회 TEST")
    void getRandomTrip() {
        List<TripDto.GetList> result = tripSearchService.getRandomTrip();
        Assertions.assertFalse(result.isEmpty());
    }
    @Test
    @DisplayName(" 내용 기반 여행 후기 추천 기능 TEST")
    void getRecommendTrip(){
        SearchHits<TripRecommendDoc> result = tripRecommendRepository.recommendByContent("여행 관련",5);
//        result.forEach(trip ->{
//            System.out.println(trip.getContent());
//        });
//        System.out.println(result.getTotalHits());
    }

}
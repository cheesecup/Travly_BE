package com.travelland.service.trip;

import com.travelland.domain.search.JamoForKorToEng;
import com.travelland.domain.trip.Trip;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripRepository;
import com.travelland.repository.trip.TripSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@SpringBootTest
//class TripSearchServiceTest {
//    @Autowired
//    private TripSearchService tripSearchService;
//    @Autowired
//    private TripSearchRepository tripSearchRepository;
//    @Autowired
//    private TripRepository tripRepository;
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private TripService tripService;
//
//    @Autowired
//    private JamoForKorToEng jamoForKorToEng;
//
//    @Test
//    void getTripList() {
//        System.out.println("tripSearchService = " + tripSearchService.getTripList(1,20,"createdAt",true));
//    }
//    @Test
//    void getTripByKeyword(){
//        System.out.println("tripSearchService = " + tripSearchService.searchTripByField("hashtag", "여행",1,20,"createdAt",true).getTotalCount());
//    }
//    @Test
//    void getRankByViewCount(){
//        System.out.println("tripSearchService = " + tripService.getRankByViewCount(10L));
//    }

//    @Test
//    void comoran(){
//        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
//        String strToAnalyze = "지난 주, 나는 울산 지역의 축제에 참여하면서 즐거운 시간을 보냈습니다. 이번 여행은 울산의 다채로운 문화와 축제 분위기를 경험하고자 마음먹고 떠난 것이었습니다.\n" +
//                "\n" +
//                "가장 먼저 울산에서 진행되는 대표적인 축제 중 하나인 '울산 어촌문화축제'에 참여했습니다. 이 축제는 울산의 어촌에서 열리며, 그곳의 특색 있는 문화와 전통을 체험할 수 있는 좋은 기회였습니다. 해안가에 설치된 작은 장터에서는 지역 특산물과 수공예품들을 구경하고 구입할 수 있었는데, 이곳에서 맛보는 간식들은 정말 맛있었습니다.\n" +
//                "\n" +
//                "또한, 축제 기간 동안 울산의 다양한 문화 행사들도 즐길 수 있었습니다. 가장 기억에 남는 것은 전통 민속놀이와 공연이었습니다. 춤과 음악이 어우러진 공연은 울산의 역사와 문화를 잘 보여주었고, 민속놀이는 참여자들과 함께 즐겁게 놀면서 소통하는 시간을 보낼 수 있었습니다.\n" +
//                "\n" +
//                "그리고 울산의 자연 경관을 즐기는 것도 잊을 수 없는 추억입니다. 축제가 열리는 동안 가까운 산을 등반하여 일출을 감상하고 해변을 걷는 등의 활동을 즐겼습니다. 특히 해안 산책로에서 바라보는 바다의 푸른 물결은 정말 황홀한 풍경이었습니다.\n" +
//                "\n" +
//                "이번 울산 지역 축제 여행은 정말 즐거웠습니다. 지역의 문화와 자연을 경험하면서 새로운 경험을 하고 많은 것을 배울 수 있었습니다. 다음에도 기회가 된다면 울산의 다른 축제에도 참여하여 더 많은 즐거움을 느끼고 싶습니다.";
//
//        KomoranResult analyzeResultList = komoran.analyze(strToAnalyze);
//
//        System.out.println(analyzeResultList.getPlainText());
//
//        List<Token> tokenList = analyzeResultList.getTokenList();
//        for (Token token : tokenList) {
//            if(token.getPos().matches("^(NP|NNP|NNG)$"))
//            System.out.format("(%2d, %2d) %s/%s\n", token.getBeginIndex(), token.getEndIndex(), token.getMorph(), token.getPos());
//        }
//    }

//    @Test
//    void getTripList() {
//    SearchHits<TripSearchDoc> res = tripSearchRepository.searchByTextTEST("서울", PageRequest.of(0,5));
//    res.getSearchHits().forEach(System.out::println);
//    res.getSearchHits().forEach(data -> System.out.println(data.getHighlightFields().keySet()));
//    }

//    @Test
//    @Transactional(readOnly = true)
//    void createTripDoc(){
//        TripDto.Get trip = tripService.getTrip(2L,"test@test.com");
//
//        tripSearchService.createTripDocument(tripRepository.findById(2L)
//                        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER)),
//                trip.getHashtagList(),trip.getImageUrlList().get(0));
//    }


//    @Test
//    void engToKorTest() {
//        tripSearchService.totalSearchTrip("ㅂㅅㄱㅇㅅ",1,5,"createdAt",true)
//                .getSearches().forEach(System.out::println);
//    }

//    @Test
//    void jamoTest() {
////        System.out.println(jamoForKorToEng.korToEng("ㅑㅔㅙㅜㄷ"));
//        System.out.println("가나 다라마".split(" ").length);
//    }
//}
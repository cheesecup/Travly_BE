package com.travelland.service.trip;

import com.travelland.domain.search.KoreanKeyboardToEng;
import com.travelland.domain.search.SearchArea;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripHashtag;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripRecommendDoc;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.elasticsearch.ElasticsearchLogService;
import com.travelland.repository.trip.TripHashtagRepository;
import com.travelland.repository.trip.TripRecommendRepository;
import com.travelland.repository.trip.TripRepository;
import com.travelland.repository.trip.TripSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j(topic = "ES")
@Service
@RequiredArgsConstructor
public class TripSearchService {
    private final TripSearchRepository tripSearchRepository;
    private final ElasticsearchLogService elasticsearchLogService;
    private final TripRepository tripRepository;
    private final TripHashtagRepository tripHashtagRepository;
    private final TripImageService tripImageService;
    private final SearchArea searchArea;
    private final KoreanKeyboardToEng koreanKeyboardToEng;
    private final TripRecommendRepository tripRecommendRepository;
    
    /**
     * 여행 정보를 검색하기 위해 Elasticsearch에 데이터 저장
     * @param trip 저장할 여행 정보
     * @param hashtag 저장할 여행 hashtag
     * @param thumbnailUrl 저장할 여행 thumbnail 이미지 Url
     */
    public void createTripDocument(Trip trip, List<String> hashtag , String thumbnailUrl){
        tripSearchRepository.save(TripSearchDoc.builder()
                .trip(trip)
                .hashtag(hashtag)
                .thumbnailUrl(thumbnailUrl)
                .build());
        tripRecommendRepository.save(new TripRecommendDoc(trip));
    }

    /**
     * 여행 정보를 통합 검색
     * @param text 검색 문장 또는 키워드
     * @param page page : 1부터 시작
     * @param size 한번에 보여줄 갯수
     * @param sortBy 정렬기준
     * @param isAsc 오름차순 여부
     * @return 검색 결과
     */
    public TripDto.SearchResult totalSearchTrip(String text, int page, int size, String sortBy, boolean isAsc) {
        String newText = changeKeyboardKorToAlphabet(text);

        SearchHits<TripSearchDoc> result =
                tripSearchRepository.searchByTextTEST(newText, this.toPageable(page, size, sortBy, isAsc));

        if (result.getTotalHits() == 0)
            return TripDto.SearchResult.builder().build();

        List<TripDto.Search> searches = new ArrayList<>();

        String areaLog = "";
        String hashtagLog = "";

        for(SearchHit<TripSearchDoc> search : result.getSearchHits()){
            searches.add(new TripDto.Search(search.getContent()));

            if(search.getHighlightFields().isEmpty())
                continue;

            for(Map.Entry<String,List<String>> res : search.getHighlightFields().entrySet()){
                String subValue = res.getValue().get(0).substring(4,res.getValue().get(0).length()-5);

                if(res.getKey().equals("area"))
                    areaLog = subValue;

                if(res.getKey().equals("hashtag"))
                   hashtagLog = subValue;
            }
        }
        if(!areaLog.isEmpty())
            elasticsearchLogService.putSearchLog("area", areaLog);

        if(!hashtagLog.isEmpty())
            elasticsearchLogService.putSearchLog("hashtagLog", hashtagLog);

        return TripDto.SearchResult.builder()
                .searches(searches)
                .totalCount(result.getTotalHits())
                .resultKeyword(text)
                .nearPlaces(getNearPlaceNames(searches.get(0).getArea()))
                .build();
    }

    /**
     * 여행 정보 제목 검색
     * @param title 검색할 여행 제목
     * @param page page : 1부터 시작
     * @param size 한번에 보여줄 갯수
     * @param sortBy 정렬기준
     * @param isAsc 오름차순 여부
     * @return 검색 결과
     */
    public TripDto.SearchResult searchTripByTitle(String title, int page, int size, String sortBy, boolean isAsc){
        SearchHits<TripSearchDoc> result =
                tripSearchRepository.searchByTitle(title,this.toPageable(page, size, sortBy, isAsc));

        return  searchMapper("title", title, result);
    }

    /**
     * 여행 정보 Elasticsearch 해당 field 기준 검색
     * @param field 검색할 field 명
     * @param value 검색할 field 값
     * @param page page : 1부터 시작
     * @param size 한번에 보여줄 갯수
     * @param sortBy 정렬기준
     * @param isAsc 오름차순 여부
     * @return 검색 결과
     */
    public TripDto.SearchResult searchTripByField(String field, String value, int page, int size, String sortBy, boolean isAsc) {
        SearchHits<TripSearchDoc> result =
                tripSearchRepository.searchByField(field, value,true,this.toPageable(page, size, sortBy, isAsc));

        return searchMapper(field, value, result);
    }

    /**
     * 여행 정보 지역 검색
     * @param area 검색할 지역명
     * @param page page : 1부터 시작
     * @param size 한번에 보여줄 갯수
     * @param sortBy 정렬기준
     * @param isAsc 오름차순 여부
     * @return 검색 결과
     */
    public TripDto.SearchResult searchTripByArea(String area, int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = this.toPageable(page, size, sortBy, isAsc);

        if(area.equals("전체"))
            return searchMapper("areaAll", area, tripSearchRepository.searchAllArea(true,pageable));

        String[] adaptedArea = searchArea.getMappingArea(area);
        SearchHits<TripSearchDoc> result = tripSearchRepository.searchByArea(adaptedArea,true,pageable);
        return searchMapper("area", area, result);
    }

    /**
     * 여행 정보 목록
     * @param page page : 1부터 시작
     * @param size 한번에 보여줄 갯수
     * @param sortBy 정렬기준
     * @param isAsc 오름차순 여부
     * @return 입력된 조건으로 목록 출력
     */
    public List<TripDto.GetList> getTripList(int page, int size, String sortBy, boolean isAsc){
        return  tripSearchRepository.findAllByIsPublic(this.toPageable(page, size, sortBy, isAsc), true)
                .map(TripDto.GetList::new).getContent();
    }

    /**
     * 랭킹 top 10 여행 정보 가져오기
     * @param keys Top10에 해당하는 tripId List
     * @return 랭킹 순서대로 출력
     */
    public List<TripDto.Top10> getRankByViewCount(List<Long> keys){
        return tripSearchRepository.findRankList(keys);
    }
    /**
     * 최근 인기 검색어 가져오기
     * @param field 인기 검색어를 조회할 field
     * @return 인기 검색어 List
     */
    public List<String> getRecentlyTopSearch(String field) {
        LocalDateTime now = LocalDateTime.now();
        List<String> resultList = elasticsearchLogService
                .getRankInRange(field, now.minusWeeks(1), now)
                .stream()
                .map(recentKeyword -> recentKeyword.get("key").toString())
                .toList();
        if (resultList.size() > 8) {
            resultList = resultList.subList(0, 8);
        }
        return resultList;
    }
    /**
     * Elasticsearch 내 문서 삭제
     * @param tripId 여행 정보 식별값
     */
    public void deleteTrip(Long tripId) {
        tripSearchRepository.deleteByTripId(tripId);
        tripRecommendRepository.deleteById(tripId);
    }
    /**
     * 내가 쓴 여행 정보 목록
     * @param page page : 1부터 시작
     * @param size 한번에 보여줄 갯수
     * @param email 작성자 email
     * @return 입력된 조건으로 목록 출력
     */
    public List<TripDto.GetList> getMyTripList(int page, int size, String email) {
        return tripSearchRepository.findByEmail(this.toPageable(page, size, "createdAt", false), email)
                .map(TripDto.GetList::new)
                .getContent();
    }
    /**
     * 무작위 여행 정보 8개 출력
     * @return Elasticsearch 내장된 랜덤 함수 사용한 결과
     */
    public List<TripDto.GetList> getRandomTrip(){
        return tripSearchRepository.getRandomList(8);
    }

    public void syncDBtoES() {
        for(Trip trip : tripRepository.findAllByIsDeleted(false)){
            List<String> hashtags = tripHashtagRepository.findAllByTrip(trip).stream().map(TripHashtag::getTitle).toList();
            this.createTripDocument(trip,hashtags,tripImageService.getTripThumbnailUrl(trip));
        }
    }

    /**
     * 페이징 처리
     * @param page page : 1부터 시작
     * @param size 한번에 보여줄 갯수
     * @param sortBy 정렬기준
     * @param isAsc 오름차순 여부
     * @return 입력된 조건으로 작성된 pageable
     */
    private Pageable toPageable(int page, int size, String sortBy, boolean isAsc){
        return PageRequest.of(page-1, size,
                Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
    }
    /**
     * 특정 field로 검색을 실행할 경우 해당하는 결과를 검색 로그에 기록하고 검색 결과를 Dto 형태로 매핑
     * @param field 매핑할 필드명
     * @param keyword 검색한 keyword
     * @param result 검색 결과
     * @return 검색 결과 형태 dto 반환
     */
    private TripDto.SearchResult searchMapper(String field, String keyword, SearchHits<TripSearchDoc> result){
        if (result.getTotalHits() == 0)
            return TripDto.SearchResult.builder().build();

        if(field.equals("hashtag") || field.equals("area"))
            elasticsearchLogService.putSearchLog(field, keyword);

        List<TripDto.Search> searches = result.get()
                .map(SearchHit::getContent).map(TripDto.Search::new).toList();

        return TripDto.SearchResult.builder()
                .searches(searches)
                .totalCount(result.getTotalHits())
                .resultKeyword(keyword)
                .nearPlaces(getNearPlaceNames(searches.get(0).getArea()))
                .build();
    }
    /**
     * 검색 결과에서 나타날 주변 여행지 목록 출력
     * @param area 기준 여행지 입력
     * @return 주변 여행지 검색 결과
     */
    private List<String> getNearPlaceNames(String area){
        log.info(area);
        return tripSearchRepository.searchByArea(area.split(" "), true,
                        PageRequest.of(0, 7))
                .stream()
                .map(SearchHit::getContent)
                .map(TripSearchDoc::getPlaceName)
                .toList();
    }
    /**
     * 한글 키보드 상태에서 입력된 영어 단어 변환 기능<br>
     * 잘못 변환될 가능성이 있으므로 변환 전 후를 공백으로 구분하여 검색<br>
     * Ex> ㅑㅔㅙㅜㄷ -> ㅑㅔㅙㅜㄷ iphone
     * @param text 입력 text
     * @return 변환후 text
     */
    private String changeKeyboardKorToAlphabet(String text){
        String newText = text;
        if(text.split(" ").length == 1 && text.matches("^[ㄱ-ㅎ가-힣]*$")){
            newText += " " + koreanKeyboardToEng.korToEng(text);
        }
        return newText;
    }
}

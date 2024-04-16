package com.travelland.service.trip;

import com.amazonaws.util.CollectionUtils;
import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.repository.trip.TripSearchRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j(topic = "ES")
@Service
@RequiredArgsConstructor
public class TripSearchService {
    private final TripSearchRepository tripSearchRepository;
    private final RestHighLevelClient client;

    private final StringRedisTemplate redisTemplate;
    private ZSetOperations<String, String> zSetOperations;

    @PostConstruct
    public void init() {
        zSetOperations = redisTemplate.opsForZSet();
    }

    private static final String TOTAL_ELEMENTS = "trip:totalElements";

    public void createTripDocument(Trip trip, List<String> hashtag, Member member, String thumbnailUrl, String profileUrl){
        tripSearchRepository.save(new TripSearchDoc(trip, hashtag, member, thumbnailUrl, profileUrl));
    }

    public TripDto.SearchResult searchTripByTitle(String title, int page, int size, String sortBy, boolean isAsc){

        SearchHits<TripSearchDoc> result = tripSearchRepository.searchByTitle(title,
                PageRequest.of(page-1, size,
                        Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)));
        if(result.getTotalHits() == 0)
            return TripDto.SearchResult.builder().build();

        List<TripDto.Search> searches = result.get()
                .map(SearchHit::getContent).map(TripDto.Search::new).toList();

        String[] strs = searches.get(0).getAddress().split(" ");
        String addr = strs[0] + " " + strs[1];

        return TripDto.SearchResult.builder()
                .searches(searches)
                .totalCount(result.getTotalHits())
                .resultAddress(addr)
                .nearPlaces(tripSearchRepository.searchByAddress(addr))
                .build();
    }

    public List<String> searchTripByAddress(String address){
        return tripSearchRepository.searchByAddress(address);
    }

    public TripDto.SearchResult searchTripByHashtag(String hashtag, int page, int size, String sortBy, boolean isAsc) {

        SearchHits<TripSearchDoc> result = tripSearchRepository.searchByHashtag(hashtag,
                PageRequest.of(page-1, size,
                        Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)));

        if (result.getTotalHits() == 0)
            return TripDto.SearchResult.builder().build();

        putSearchLog(hashtag,"java@java.com");

        List<TripDto.Search> searches = result.get()
                .map(SearchHit::getContent).map(TripDto.Search::new).toList();

        String[] strs = searches.get(0).getAddress().split(" ");
        String addr = strs[0] + " " + strs[1];

        return TripDto.SearchResult.builder()
                .searches(searches)
                .totalCount(result.getTotalHits())
                .resultAddress(addr)
                .nearPlaces(tripSearchRepository.searchByAddress(addr))
                .build();
    }

    //여행정복 목록 조회
    public List<TripDto.GetList> getTripList(int page, int size, String sortBy, boolean isAsc){
        return tripSearchRepository.findAll(PageRequest.of(page-1, size,
                Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy))).map(TripDto.GetList::new).getContent();
    }

    public void putSearchLog(String query,String memberId){
        String indexName = "query-log";
        IndexRequest request = new IndexRequest(indexName);

        Map<String,Object> doc = new HashMap<>();
        doc.put("query", query);
        doc.put("memberId", memberId);

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String stamp = sdf.format(date);
        doc.put("@timestamp", stamp);

        request.source(doc);

        try {
            client.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>()
            {

                @Override
                public void onResponse(IndexResponse response) {
                    log.debug("logging success");
                    log.debug(response.toString());
                }

                @Override
                public void onFailure(Exception e) {
                    log.debug("logging failed");
                    log.error(e.getMessage());
                }

            });
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<TripDto.Rank> getRecentlyTopSearch() throws IOException {
        String indexName = "query-log";

        // 최근 및 과거 시간 범위 설정
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        //LocalDateTime.now().minusWeeks(1)


        List<Map<String, Object>> recentKeywords = getKeywordsInRange(indexName, pastTime, now);
        List<Map<String, Object>> pastKeywords = getKeywordsInRange(indexName, LocalDateTime.now().minusDays(2), pastTime);

        return recentKeywords.stream()
                .map(recentKeyword -> {
                    String key = (String) recentKeyword.get("key");
                    long count = (long) recentKeyword.get("count");
                    return TripDto.Rank.builder()
                            .key(key)
                            .count(count)
                            .status(determineStatus(key, count, pastKeywords))
                            .value(determineValue(key, pastKeywords))
                            .build();
                }).toList();
    }

    public void deleteTrip(Long tripId) {
        tripSearchRepository.deleteByTripId(tripId);
    }
    
    //내가 작성한 여행정보 게시글 목록 조회
    public List<TripDto.GetList> getMyTripList(int page, int size, String email) {
        return tripSearchRepository.findByEmail(PageRequest.of(page-1, size,
                Sort.by(Sort.Direction.DESC, "createdAt")), email).map(TripDto.GetList::new).getContent();
    }
    
    //여행정보 조회수 top10 목록 조회
    public List<TripDto.GetList> getTripListTop10() {
        return getTop10Ids().stream().map(id -> new TripDto.GetList(tripSearchRepository.findByTripId(id))).toList();
    }

    public void increaseViewCount(Long tripId) {
        TripSearchDoc tripSearchDoc = tripSearchRepository.findByTripId(tripId);
        tripSearchDoc.increaseViewCount();
        tripSearchRepository.save(tripSearchDoc);
    }

    private List<Map<String, Object>> getKeywordsInRange(String indexName, LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0); // 인기검색어 1~10위
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchSourceBuilder.query(QueryBuilders.rangeQuery("@timestamp")
                .gte(startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .lte(endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_query").field("query.keyword");
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Terms byQuery = searchResponse.getAggregations().get("by_query");

        return byQuery.getBuckets().stream()
                .map(entry -> {
                    Map<String, Object> keywordEntry = new HashMap<>();
                    keywordEntry.put("key", entry.getKeyAsString());
                    keywordEntry.put("count", entry.getDocCount());
                    return keywordEntry;
                }).toList();
    }


    private String determineStatus(String key, long count, List<Map<String, Object>> pastKeywords) {
        for (Map<String, Object> pastKeyword : pastKeywords) {
            if (key.equals(pastKeyword.get("key"))) {
                long pastCount = (long) pastKeyword.get("count");
                return count > pastCount ? "up" : (count == pastCount ? "-" : "down");
            }
        }
        return "new";
    }

    private int determineValue(String key, List<Map<String, Object>> pastKeywords) {
        for (int i = 0; i < pastKeywords.size(); i++) {
            Map<String, Object> pastKeyword = pastKeywords.get(i);
            if (key.equals(pastKeyword.get("key"))) {
                return i;
            }
        }
        return 0;
    }
    
    //여행정보 조회수 top 10 tripId 조회
    private List<Long> getTop10Ids() {
        Set<String> ids = zSetOperations.reverseRange("tripViewRank", 0, 9);

        if (CollectionUtils.isNullOrEmpty(ids)) {
            return new ArrayList<>();
        }

        return ids.stream().map(Long::parseLong).collect(Collectors.toList());
    }
}
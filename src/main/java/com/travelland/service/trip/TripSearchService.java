package com.travelland.service.trip;

import com.travelland.document.TripSearchDoc;
import com.travelland.dto.TripSearchDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.trip.TripSearchRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "ES")
@Service
@RequiredArgsConstructor
public class TripSearchService {
    private final TripSearchRepository tripSearchRepository;
    private final RestHighLevelClient client;

    private static final String TOTAL_ELEMENTS = "trip:totalElements";

    public TripSearchDto.GetResponse createTripDocument(TripSearchDto.CreateRequest tripSearchDto){
        return new TripSearchDto.GetResponse(
                tripSearchRepository.save(new TripSearchDoc(tripSearchDto)));
    }
    
    // elastic의 id로 조회
    public TripSearchDto.GetResponse searchTripById(Long tripId){
        TripSearchDoc tripSearchDoc = tripSearchRepository.findById(tripId).
                orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
        return new TripSearchDto.GetResponse(tripSearchDoc);
    }
    
    // DB의 trip id로 조회
    public TripSearchDto.GetResponse searchTripByTripId(Long tripId) {
        TripSearchDoc tripSearchDoc = tripSearchRepository.findByTripId(tripId).
                orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        return new TripSearchDto.GetResponse(tripSearchDoc);
    }

    public void searchTripByTitle(String title){
        Pageable pageable = PageRequest.of(0, 10);
        Page<TripSearchDoc> page = tripSearchRepository.searchByTitle("이색", pageable);
        log.info(String.valueOf(page.getTotalElements()));
        log.info(String.valueOf(page.getContent().get(0)));
    }

    public Page<TripSearchDoc> searchTripByHashtag(String hashtag) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TripSearchDoc> page = tripSearchRepository.searchByHashtag(hashtag, pageable);
        if (page.getTotalElements() > 0)
            putSearchLog(hashtag,"java@java.com");
        return page;
    }

    public List<TripSearchDoc> searchTripByHashtags(String hashtag) {
        Pageable pageable = PageRequest.of(5, 20);
        return tripSearchRepository.searchByHashtags(hashtag, pageable);
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

    public List<TripSearchDto.RankResponse> getPopwordList() throws IOException {
        String indexName = "query-log";

        // 최근 및 과거 시간 범위 설정
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        //LocalDateTime.now().minusWeeks(1)

        // 최근 검색어 가져오기
        List<Map<String, Object>> recentKeywords = getKeywordsInRange(indexName, pastTime, now);

        // 이전 검색어 가져오기
        List<Map<String, Object>> pastKeywords = getKeywordsInRange(indexName, LocalDateTime.now().minusDays(2), pastTime);

        return recentKeywords.stream()
                .map(recentKeyword -> {
                    String key = (String) recentKeyword.get("key");
                    long count = (long) recentKeyword.get("count");
                    return TripSearchDto.RankResponse.builder()
                            .key(key)
                            .count(count)
                            .status(determineStatus(key, count, pastKeywords))
                            .value(determineValue(key, pastKeywords))
                            .build();
                }).toList();
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
}

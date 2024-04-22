package com.travelland.global.elasticsearch;

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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "ESLog")
@Component
@RequiredArgsConstructor
public class ElasticsearchLogService {
    private final RestHighLevelClient client;
    public void indexDocument(String indexName, Map<String,Object> doc) {
        IndexRequest request = new IndexRequest(indexName).source(doc);
        try {
            client.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
                @Override
                public void onResponse(IndexResponse response) {
                    log.debug(response.toString());
                }
                @Override
                public void onFailure(Exception e) {
                    log.error(e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void putSearchLog(String field, String query) {
        String indexName = "query-log";
        Map<String,Object> doc = new HashMap<>();
        doc.put("field", field);
        doc.put("query", query);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        doc.put("@timestamp", sdf.format(new Date(System.currentTimeMillis())));
        indexDocument(indexName, doc);
    }

    public List<Map<String, Object>> getRankInRange(String field, LocalDateTime startTime, LocalDateTime endTime){
        SearchRequest searchRequest = new SearchRequest("query-log");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchSourceBuilder.query(QueryBuilders.rangeQuery("@timestamp")
                .gte(startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .lte(endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_query").field("query."+field);
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Terms byQuery = searchResponse.getAggregations().get("by_query");

            return byQuery.getBuckets().stream().map(this::keywordEntryMapper).toList();

        }catch (IOException e){
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    private Map<String, Object> keywordEntryMapper(Terms.Bucket entry){
        Map<String, Object> keywordEntry = new HashMap<>();
        keywordEntry.put("key", entry.getKeyAsString());
        keywordEntry.put("count", entry.getDocCount());
        return keywordEntry;
    }
}


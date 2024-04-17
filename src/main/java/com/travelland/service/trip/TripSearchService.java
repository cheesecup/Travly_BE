package com.travelland.service.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripHashtag;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.elasticsearch.ElasticsearchLogService;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.job.DataSet;
import com.travelland.repository.trip.TripRepository;
import com.travelland.repository.trip.TripSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j(topic = "ES")
@Service
@RequiredArgsConstructor
public class TripSearchService {
    private final TripSearchRepository tripSearchRepository;
    private final ElasticsearchLogService elasticsearchLogService;
    private final TripRepository tripRepository;

    public void createTripDocument(Trip trip, List<String> hashtag, Member member, String thumbnail){
        tripSearchRepository.save(new TripSearchDoc(trip, hashtag, member, thumbnail));
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

        elasticsearchLogService.putSearchLog(hashtag,"java@java.com");

        List<TripDto.Search> searches = result.get()
                .map(SearchHit::getContent).map(TripDto.Search::new).toList();
        String[] strs = searches.get(0).getAddress().split(" ");
        String addr = strs[0];

        if (strs.length >= 2)
            addr += " " + strs[1];

        return TripDto.SearchResult.builder()
                .searches(searches)
                .totalCount(result.getTotalHits())
                .resultAddress(addr)
                .nearPlaces(tripSearchRepository.searchByAddress(addr))
                .build();
    }

    public List<TripDto.GetList> getTripList(int page, int size, String sortBy, boolean isAsc){
        return  tripSearchRepository.findAllByIsPublic(
                PageRequest.of(page-1, size,
                Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)), true)
                .map(trip -> new TripDto.GetList(trip, trip.getThumbnailUrl())).getContent();
    }


    public List<TripDto.Rank> getRecentlyTopSearch() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastTime = now.minusDays(1);
        //LocalDateTime.now().minusWeeks(1)

        List<Map<String, Object>> pastKeywords = elasticsearchLogService
                .getRankInRange("keyword", pastTime.minusDays(1), pastTime);

        return elasticsearchLogService.
                getRankInRange("keyword", pastTime, now)
                .stream()
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

    public List<TripDto.GetList> getMyTripList(int page, int size, String email) {
        return tripSearchRepository.findByEmail(PageRequest.of(page-1, size,
                Sort.by(Sort.Direction.DESC, "createdAt")), email)
                .map(trip -> new TripDto.GetList(trip, trip.getThumbnailUrl()))
                .getContent();
    }

    public void increaseViewCount(Long tripId) {
        TripSearchDoc tripSearchDoc = tripSearchRepository.findByTripId(tripId);
        tripSearchDoc.increaseViewCount();
        tripSearchRepository.save(tripSearchDoc);
    }

    public List<DataSet> readTripViewCount(int page, int size){
        return  tripSearchRepository.readViewCount(PageRequest.of(page, size));
    }
    public Long readTotalCount(){
        return tripSearchRepository.count();
    }

    public void syncViewCount(List<DataSet> dataSets){
        tripRepository.updateBulkViewCount(dataSets);
    }


    private String determineStatus(String key, long count, List<Map<String, Object>> pastKeywords) {

        Optional<Map<String, Object>> matchingKeyword = pastKeywords.stream()
                .filter(keyword -> key.equals(keyword.get("key")))
                .findFirst();

        if (matchingKeyword.isEmpty())
            return "new";

        long pastCount = (long) matchingKeyword.get().get("count");

        if (count > pastCount)
            return "up";

        if(count < pastCount)
            return "down";

        return "-";
    }

    private int determineValue(String key, List<Map<String, Object>> pastKeywords) {
        return IntStream.range(0, pastKeywords.size())
                .filter(i -> key.equals(pastKeywords.get(i).get("key")))
                .findFirst().orElse(0);
    }

}
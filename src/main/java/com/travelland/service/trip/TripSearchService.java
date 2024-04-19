package com.travelland.service.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.elasticsearch.ElasticsearchLogService;
import com.travelland.global.job.DataIntSet;
import com.travelland.global.job.DataStrSet;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripRepository;
import com.travelland.repository.trip.TripSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.travelland.constant.Constants.SEARCH_RANK_FIELD;

@Slf4j(topic = "ES")
@Service
@RequiredArgsConstructor
public class TripSearchService {
    private final TripSearchRepository tripSearchRepository;
    private final ElasticsearchLogService elasticsearchLogService;

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

        elasticsearchLogService.putSearchLog(hashtag);

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
                .map(TripDto.GetList::new).getContent();
    }

    public List<TripDto.GetList> getRankByViewCount(List<Long> keys){
        return tripSearchRepository.findRankList(keys);
    }

    public List<TripDto.Rank> getRecentlyTopSearch() {
        LocalDateTime now = LocalDateTime.now();
        return elasticsearchLogService.
                getRankInRange(SEARCH_RANK_FIELD, now.minusWeeks(1), now)
                .stream()
                .map(this::rankMapper)
                .toList();
    }

    public void deleteTrip(Long tripId) {
        tripSearchRepository.deleteByTripId(tripId);
    }

    public List<TripDto.GetList> getMyTripList(int page, int size, String email) {
        return tripSearchRepository.findByEmail(PageRequest.of(page-1, size,
                Sort.by(Sort.Direction.DESC, "createdAt")), email)
                .map(TripDto.GetList::new)
                .getContent();
    }

    public List<DataIntSet> readTripViewCount(int page, int size){
        return  tripSearchRepository.readViewCount(PageRequest.of(page, size));
    }
    public Long readTotalCount(){
        return tripSearchRepository.count();
    }

    private TripDto.Rank rankMapper(Map<String, Object> recentKeyword){
        return TripDto.Rank.builder()
                .key((String) recentKeyword.get("key"))
                .count((long) recentKeyword.get("count"))
                .build();
    }
}
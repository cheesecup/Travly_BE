package com.travelland.service.trip;

import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripArea;
import com.travelland.domain.trip.TripHashtag;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.elasticsearch.ElasticsearchLogService;
import com.travelland.repository.trip.TripHashtagRepository;
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
import java.util.List;

@Slf4j(topic = "ES")
@Service
@RequiredArgsConstructor
public class TripSearchService {
    private final TripSearchRepository tripSearchRepository;
    private final ElasticsearchLogService elasticsearchLogService;
    private final TripRepository tripRepository;
    private final TripHashtagRepository tripHashtagRepository;
    private final TripImageService tripImageService;
    private final TripArea tripArea;

    public void createTripDocument(Trip trip, List<String> hashtag , String thumbnailUrl){
        tripSearchRepository.save(new TripSearchDoc(trip, hashtag, thumbnailUrl));
    }

    public TripDto.SearchResult searchTrip(String text, int page, int size, String sortBy, boolean isAsc) {
        SearchHits<TripSearchDoc> result = tripSearchRepository.searchByText(text,
                PageRequest.of(page - 1, size,
                        Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)));
        return searchMapper("text", text, result);
    }

    public TripDto.SearchResult searchTripByTitle(String title, int page, int size, String sortBy, boolean isAsc){
        SearchHits<TripSearchDoc> result = tripSearchRepository.searchByTitle(title,
                PageRequest.of(page-1, size,
                        Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)));
        return  searchMapper("title",title,result);
    }

    public TripDto.SearchResult searchTripByField(String field, String value, int page, int size, String sortBy, boolean isAsc) {
        SearchHits<TripSearchDoc> result = tripSearchRepository.searchByField(field, value,true,
                PageRequest.of(page-1, size,
                        Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)));
        return searchMapper(field, value, result);
    }

    public TripDto.SearchResult searchTripByArea(String area, int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = PageRequest.of(page-1, size,
                Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));

        if(area.equals("전체"))
            return searchMapper("areaAll", area, tripSearchRepository.searchAllArea(true,pageable));

        String[] adaptedArea = tripArea.getMappingArea(area);
        SearchHits<TripSearchDoc> result = tripSearchRepository.searchByArea(adaptedArea,true,pageable);
        return searchMapper("area", area, result);
    }

    public List<TripDto.GetList> getTripList(int page, int size, String sortBy, boolean isAsc){
        return  tripSearchRepository.findAllByIsPublic(
                PageRequest.of(page-1, size,
                Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)), true)
                .map(TripDto.GetList::new).getContent();
    }

    public List<TripDto.Top10> getRankByViewCount(List<Long> keys){
        return tripSearchRepository.findRankList(keys);
    }

    public List<String> getRecentlyTopSearch(String field) {
        LocalDateTime now = LocalDateTime.now();
        return elasticsearchLogService.
                getRankInRange(field, now.minusWeeks(1), now)
                .stream()
                .map(recentKeyword -> recentKeyword.get("key").toString())
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

    public List<TripDto.GetList> getRandomTrip(){
        return tripSearchRepository.getRandomList(8);
    }

    public void syncDBtoES() {
        for(Trip trip : tripRepository.findAllByIsDeleted(false)){
         tripSearchRepository.save(new TripSearchDoc(trip,tripHashtagRepository.findAllByTrip(trip).stream().map(TripHashtag::getTitle).toList(), tripImageService.getTripThumbnailUrl(trip)
         ));
        }
    }

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

    private List<String> getNearPlaceNames(String area){
        return tripSearchRepository.searchByField("area", area, true,
                        PageRequest.of(0, 7))
                .stream()
                .map(SearchHit::getContent)
                .map(TripSearchDoc::getPlaceName)
                .toList();
    }
}
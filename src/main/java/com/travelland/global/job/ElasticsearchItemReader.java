package com.travelland.global.job;

import com.travelland.service.trip.TripSearchService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

import java.util.List;
@Slf4j(topic = "ES Item Reader : ")
public class ElasticsearchItemReader implements ItemReader<List<DataIntSet>> {

    private final TripSearchService tripSearchService;
    private final int pageSize;
    private int currentPage;
    private final long totalPage;

    @Builder
    public ElasticsearchItemReader(TripSearchService tripSearchService, int pageSize) {
        this.tripSearchService = tripSearchService;
        this.pageSize = pageSize;
        this.totalPage = (long) Math.ceil((double) tripSearchService.readTotalCount() / pageSize);
    }

    @Override
    public List<DataIntSet> read() {
        log.info("currentPage: " + currentPage);
        log.info("totalPage: " + totalPage);

        if(totalPage == 0)
            return null;

        if(currentPage > totalPage)
            return null;

        List<DataIntSet> result = tripSearchService.readTripViewCount(currentPage++, pageSize);
        if(result.isEmpty())
            return null;

        return result;
    }
}

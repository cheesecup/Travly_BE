package com.travelland.global.job;

import java.util.List;
/**
 * 추천 컨텐츠를 위한 dataSet <br>
 * id: 추천 기준 tripId <br>
 * recommendIds: 추천 결과 tripId 리스트
 *
 * @author     kjw
 * @version    1.0.0
 * @since      1.0.0
 */
public class DataSet {
    /**
     * 추천 기준 tripId
     */
    private Long id;
    /**
     * 추천 결과 tripId 리스트
     */
    private List<String> recommendIds;

    public DataSet(Long id, List<String> recommendIds){
        this.id = id;
        this.recommendIds = recommendIds;
    }
    public Long getId(){
        return this.id;
    }
    public List<String> getRecommendIds(){
        return this.recommendIds;
    }

    @Override
    public String toString() {
        return "{id=" + id + ", value=" + recommendIds + "}";
    }
}

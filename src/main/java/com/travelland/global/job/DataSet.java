package com.travelland.global.job;

import java.util.List;

public class DataSet {
    private Long id;
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

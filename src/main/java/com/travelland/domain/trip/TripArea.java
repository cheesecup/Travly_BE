package com.travelland.domain.trip;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.travelland.constant.Constants.TRIP_AREA_MAP;

@Component
public class TripArea {
    private final Map<String,String[]> areaMap;

    public TripArea(){
        this.areaMap = loadTripArea();
    }

    private Map<String,String[]> loadTripArea(){
        Map<String, String[]> area = new HashMap<>();
        for(String subArea : TRIP_AREA_MAP.split("/")) {
            String[] subStr = subArea.split("_");
            area.put(subStr[0], subStr[1].split(","));
        }
        return area;
    }

    public String[] getMappingArea(String area){
        if(this.areaMap.containsKey(area))
            return this.areaMap.get(area);

        return new String[]{area};
    }
}

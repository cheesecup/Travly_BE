package com.travelland.domain.trip;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.travelland.constant.Constants.TRIP_AREA_MAP;

@Component
public class TripArea {
    private final Map<String,List<String>> areaMap;

    public TripArea(){
        this.areaMap = loadTripArea();
    }

    private Map<String,List<String>> loadTripArea(){
        Map<String, List<String>> area = new HashMap<>();
        for(String subArea : TRIP_AREA_MAP.split("/")) {
            String[] subStr = subArea.split(",");

            if (area.containsKey(subStr[1])){
                area.get(subStr[1]).add(subStr[0]);
                continue;
            }

            List<String> subList = new ArrayList<>();
            subList.add(subStr[0]);
            area.put(subStr[1], subList);
        }
        return area;
    }

    public List<String> getMappingArea(String area){
        if(this.areaMap.containsKey(area))
            return this.areaMap.get(area);

        List<String> res = new ArrayList<>();
        res.add(area);
        return res;
    }
}

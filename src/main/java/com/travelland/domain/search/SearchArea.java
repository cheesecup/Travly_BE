package com.travelland.domain.search;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.travelland.constant.Constants.TRIP_AREA_MAP;
/**
 * 카테고리 형태의 지역 검색 결과를 보여주기 위한 매핑 클래스 <br><br>
 * EX> 서을 -> [서울시, 서울, 서울특별시]
 * @author     kjw
 * @version    1.0.0
 * @since      1.0.0
 */
@Component
public class SearchArea {
    /**
     * key: 검색 입력에 해당하는 값(서울)<br>
     * value: 검색 대상에 해당하는값({서울시, 서울, 서울특별시})
     */
    private final Map<String,String[]> areaMap;

    /**
     * 빈 주입시 최초 1번 문자열을 Map 형태로 변환
     */
    public SearchArea(){
        this.areaMap = loadTripArea();
    }
    /**
     * 매핑에 해당하는 문자열을 Load 하여 Map으로 변환
     * @return key: 카테고리에 해당하는 지역명, value: 검색 대상 문자열 배열
     */
    private Map<String,String[]> loadTripArea(){
        Map<String, String[]> area = new HashMap<>();
        for(String subArea : TRIP_AREA_MAP.split("/")) {
            String[] subStr = subArea.split("_");
            area.put(subStr[0], subStr[1].split(","));
        }
        return area;
    }
    /**
     * 검색 카테고리에 해당하는 값이 들어오면 관련 문자열 배열을 반환
     * @param area 카테고리에 해당하는 지역명
     * @return 검색 대상 문자열 배열
     */
    public String[] getMappingArea(String area){
        if(this.areaMap.containsKey(area))
            return this.areaMap.get(area);

        return new String[]{area};
    }
}

package com.travelland.constant;

public final class Constants {
    private Constants() {
    }
    public static final String TRIP_TOTAL_ELEMENTS = "trip:totalElements";
    public static final String TRIP_VIEW_COUNT = "tripViewCount:";
    public static final String VIEW_RANK = "tripViewRank";
    public static final String TRIP_LIKES_TRIP_ID = "tripLikes:tripId:";
    public static final String TRIP_SCRAPS_TRIP_ID = "tripScraps:tripId:";
    public static final String PLAN_VIEW_COUNT = "planViewCount:";
    public static final String PLAN_LIKES_PLAN_ID = "planLikes:planId:";
    public static final String PLAN_SCRAPS_PLAN_ID = "planScraps:planId:";
    public static final String PLAN_TOTAL_COUNT = "plan_total_count:";
    /**
     * 여행 추천 관련 batch Job Name
     */
    public static final String TRIP_RECOMMEND_JOB_NAME = "tripRecommend";
    /**
     * 카테고리 형태의 지역명을 검색하기 위한 매핑 문자열<br>
     * /로 서로다른 카테고리 구분 <br>
     * _로 key 와 value를 구분 <br>
     * ,로 String을 구분
     */
    public static final String TRIP_AREA_MAP = "서울_서울,서울특별시,서울시/경기_경기,경기도/인천_인천,인천시,인천특별시/" +
            "강원_강원,강원도/대전_대전,대전광역시,대전시/충북충남_충북,충남,충청도,충청북도,충청남도/" +
            "경북경남_경북,경상북도,경남,경상남도,경상,경상도/부산_부산,부산광역시,부산시/울산_울산,울산시,울산광역시/" +
            "전북전남_전북,전라북도,전남,전라남도,전라도,전라/제주_제주,제주시,제주도,제주특별시";
}
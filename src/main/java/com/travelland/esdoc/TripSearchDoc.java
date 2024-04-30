package com.travelland.esdoc;

import com.travelland.domain.trip.Trip;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Document(indexName = "tripdoc" )
public class TripSearchDoc {

    @Id
    @Field(name = "id", type = FieldType.Keyword)
    private String id;

    @Field(name = "trip_id", type = FieldType.Long)
    private Long tripId;

    @Field(name = "title",type = FieldType.Text , copyTo = {"eng_kor_title_suggest", "chosung_title"}
            ,analyzer = "korean_analyzer")
    private String title;

    @Field(name = "eng_kor_title_suggest", type = FieldType.Text, analyzer = "korean_analyzer",
            searchAnalyzer = "eng2kor_analyzer")
    private String engKorTitleSuggest;

    @Field(name = "chosung_title",type = FieldType.Text, analyzer = "chosung_analyzer", searchAnalyzer = "standard")
    private String chosungTitle;

    @Field(name = "content",type = FieldType.Text, analyzer = "korean_analyzer")
    private String content;

    @Field(name = "cost",type = FieldType.Integer)
    private int cost;

    @Field(name = "area", type = FieldType.Keyword, copyTo = {"eng_kor_area_suggest", "chosung_area"})
    private String area;

    @Field(name = "eng_kor_area_suggest", type = FieldType.Keyword, searchAnalyzer = "eng2kor_analyzer")
    private String engKorAreaSuggest;

    @Field(name = "chosung_area", type = FieldType.Keyword,  analyzer = "chosung_analyzer",
            searchAnalyzer = "keyword")
    private String chosungArea;

    @Field(name = "hashtag", type = FieldType.Keyword, copyTo = {"eng_kor_hashtag_suggest", "chosung_hashtag"})
    private List<String> hashtag;

    @Field(name = "eng_kor_hashtag_suggest", type = FieldType.Keyword, searchAnalyzer = "eng2kor_analyzer")
    private List<String> engKorHashtagSuggest;

    @Field(name = "chosung_hashtag", type = FieldType.Keyword,  analyzer = "chosung_analyzer",
            searchAnalyzer = "keyword")
    private List<String> chosungHashtag;

    @Field(name = "trip_start_date", type = FieldType.Date, format = {DateFormat.date, DateFormat.epoch_millis})
    private LocalDate tripStartDate;

    @Field(name = "trip_end_date", type = FieldType.Date, format = {DateFormat.date, DateFormat.epoch_millis})
    private LocalDate tripEndDate;

    @Field(name = "created_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    @Field(name = "address", type = FieldType.Text)
    private String address;

    @Field(name = "place_name", type = FieldType.Keyword)
    private String placeName;

    @Field(name = "thumbnail_url", type = FieldType.Keyword)
    private String thumbnailUrl;

    @Field(name = "email", type = FieldType.Keyword)
    private String email;

    @Field(name = "is_public", type = FieldType.Boolean)
    private Boolean isPublic;


    @Builder
    public TripSearchDoc(Trip trip, List<String> hashtag, String thumbnailUrl) {
        this.tripId =trip.getId();
        this.title = trip.getTitle();
        this.cost = trip.getCost();
        this.area = trip.getArea();
        this.hashtag = hashtag;
        this.tripStartDate = trip.getTripStartDate();
        this.tripEndDate = trip.getTripEndDate();
        this.content = makeShortContent(trip.getContent(),200);
        this.createdAt = trip.getCreatedAt();
        this.address = trip.getAddress();
        this.placeName = trip.getPlaceName();
        this.thumbnailUrl = thumbnailUrl;
        this.email = trip.getMember().getEmail();
        this.isPublic = trip.isPublic();
    }
    private String makeShortContent(String content, int length){
        if(content.length() > length)
            return content.substring(0,length-1);
        return content;
    }

    public void update(Trip trip, List<String> hashtag, String thumbnailUrl) {
        this.title = trip.getTitle();
        this.cost = trip.getCost();
        this.area = trip.getArea();
        this.hashtag = hashtag;
        this.tripStartDate = trip.getTripStartDate();
        this.tripEndDate = trip.getTripEndDate();
        this.address = trip.getAddress();
        this.thumbnailUrl = thumbnailUrl;
        this.isPublic = trip.isPublic();
    }
}
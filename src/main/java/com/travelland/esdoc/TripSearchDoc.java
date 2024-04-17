package com.travelland.esdoc;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@Document(indexName = "tripdocs" )
@Setting(settingPath = "static/es-setting.json")
@Mapping(mappingPath = "static/es-mapping.json")
public class TripSearchDoc {

    @Id
    @Field(name = "id", type = FieldType.Keyword)
    private String id;

    @Field(name = "trip_id", type = FieldType.Long)
    private Long tripId;

    @Field(name = "title",type = FieldType.Text , analyzer = "nori")
    private String title;

    @Field(name = "content",type = FieldType.Text)
    private String content;

    @Field(name = "cost",type = FieldType.Integer)
    private int cost;

    @Field(name = "area", type = FieldType.Keyword)
    private String area;

    @Field(name = "nickname", type = FieldType.Keyword)
    private String nickname;

    @Field(name = "hashtag", type = FieldType.Keyword)
    private List<String> hashtag;

    @Field(name = "trip_start_date", type = FieldType.Date, format = {DateFormat.basic_date, DateFormat.epoch_millis})
    private LocalDate tripStartDate;

    @Field(name = "trip_end_date", type = FieldType.Date, format = {DateFormat.basic_date, DateFormat.epoch_millis})
    private LocalDate tripEndDate;

    @Field(name = "created_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    @Field(name = "address", type = FieldType.Text)
    private String address;

//    @Field(name = "place_name", type = FieldType.Keyword)
//    private String placeName;

    @Field(name = "profile_url", type = FieldType.Keyword)
    private String profileUrl;

    @Field(name = "view_count", type = FieldType.Integer)
    private int viewCount;

    @Field(name = "thumbnail_url", type = FieldType.Keyword)
    private String thumbnailUrl;

    @Field(name = "email", type = FieldType.Keyword)
    private String email;

    @Field(name = "is_public", type = FieldType.Boolean)
    private Boolean isPublic;


    @Builder
    public TripSearchDoc(Trip trip, List<String> hashtag, Member member, String thumbnailUrl, String profileUrl) {
        this.tripId =trip.getId();
        this.title = trip.getTitle();
        this.cost = trip.getCost();
        this.area = trip.getArea();
        this.hashtag = hashtag;
        this.tripStartDate = trip.getTripStartDate();
        this.tripEndDate = trip.getTripEndDate();
        this.content = makeShortContent(trip.getContent(),100);
        this.createdAt = LocalDateTime.now();
        this.address = trip.getAddress();
        this.nickname = member.getNickname();
        this.profileUrl = profileUrl;
        this.viewCount = trip.getViewCount();
        this.thumbnailUrl = thumbnailUrl;
        this.email = member.getEmail();
        this.isPublic = trip.isPublic();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    private String makeShortContent(String content, int length){
        if(content.length() > length)
            return content.substring(0,length-1);
        return content;
    }
}
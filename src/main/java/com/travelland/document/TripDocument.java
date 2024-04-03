package com.travelland.document;

import com.travelland.dto.TripSearchDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "tripdocs" )
@Setting(settingPath = "logs/es-setting.json")
@Mapping(mappingPath = "logs/es-mapping.json")
public class TripDocument {

    @Id
    @Field(name = "id", type = FieldType.Keyword)
    private String id;

    @Field(name = "trip_id", type = FieldType.Long)
    private Long tripId;

    @Field(name = "title",type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(name = "cost",type = FieldType.Integer)
    private int cost;

    @Field(name = "area", type = FieldType.Keyword)
    private String area;

    @Field(name = "hashtag", type = FieldType.Keyword)
    private List<String> hashtag;

    @Field(name = "trip_start_date", type = FieldType.Date, format = {DateFormat.basic_date, DateFormat.epoch_millis})
    private LocalDate tripStartDate;

    @Field(name = "trip_end_date", type = FieldType.Date, format = {DateFormat.basic_date, DateFormat.epoch_millis})
    private LocalDate tripEndDate;

    @Field(name = "created_at", type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Field(name = "location", type = FieldType.Object)
    @GeoPointField
    private GeoPoint location;


    @Builder
    public TripDocument(TripSearchDto.CreateRequest requestDto) {
        this.tripId =requestDto.getTripId();
        this.title = requestDto.getTitle();
        this.cost = requestDto.getCost();
        this.area = requestDto.getArea();
        this.hashtag = requestDto.getHashtag();
        this.tripStartDate = requestDto.getTripStartDate();
        this.tripEndDate = requestDto.getTripEndDate();
        this.location = requestDto.getLocation();
        this.createdAt = toLocalDateTime();
    }

    private LocalDateTime toLocalDateTime(){
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
}

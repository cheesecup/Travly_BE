package com.travelland.document;

import com.travelland.dto.TripSearchDto;
import lombok.*;

import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "tripdocs" )
@Setting(settingPath = "static/es-setting.json")
@Mapping(mappingPath = "static/es-mapping.json")
public class TripDocument {

    @Id
    @Field(name = "trip_id", type = FieldType.Keyword)
    private String id;

    @Field(name = "title",type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(name = "content",type = FieldType.Text)
    private String content;

    @Field(name = "cost",type = FieldType.Integer)
    private int cost;

    @Field(name = "area", type = FieldType.Keyword)
    private String area;

    @Field(name = "hashtag", type = FieldType.Keyword)
    private String hashtag;

    @Field(name = "trip_start_date", type = FieldType.Date, format = {DateFormat.basic_date, DateFormat.epoch_millis})
    private LocalDate tripStartDate;

    @Field(name = "trip_end_date", type = FieldType.Date, format = {DateFormat.basic_date, DateFormat.epoch_millis})
    private LocalDate tripEndDate;

    @Field(name = "created_at", type = FieldType.Date, format = {DateFormat.basic_date_time, DateFormat.epoch_millis})
    private LocalDateTime createdAt;

    @Field(name = "location", type = FieldType.Object)
    @GeoPointField
    private GeoPoint location;

    @Builder
    public TripDocument(TripSearchDto.CreateRequest tripSearchDto) {
        this.title = tripSearchDto.getTitle();
        this.content = tripSearchDto.getContent();
        this.cost = tripSearchDto.getCost();
        this.area = tripSearchDto.getArea();
        this.tripStartDate = tripSearchDto.getTripStartDate();
        this.tripEndDate = tripSearchDto.getTripEndDate();
        this.createdAt = LocalDateTime.now();
    }

}

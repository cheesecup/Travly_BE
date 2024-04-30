package com.travelland.esdoc;

import com.travelland.domain.trip.Trip;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@Document(indexName = "trip_recommend")
public class TripRecommendDoc {
    @Id
    @Field(name = "id", type = FieldType.Long)
    private Long id;

    @Field(name = "content",type = FieldType.Text, analyzer = "openkoreantext_custom_analyzer", searchAnalyzer = "openkoreantext_custom_analyzer")
    private String content;

    @Field(name = "is_public", type = FieldType.Boolean)
    private Boolean isPublic;

    public TripRecommendDoc(Trip trip){
        this.id = trip.getId();
        this.content = trip.getContent();
        this.isPublic = trip.isPublic();
    }
}

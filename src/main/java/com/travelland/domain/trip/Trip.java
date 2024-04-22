package com.travelland.domain.trip;

import com.travelland.domain.member.Member;
import com.travelland.dto.trip.TripDto;
import com.travelland.dto.trip.TripDto.Create;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(length = 1000)
    private String content;

    private int cost;

    @Column(length = 30)
    private String area; //여행 지역

    private String address; //도로명 주소

    private String placeName; //여행장소

    private boolean isPublic;

    private LocalDate tripStartDate;

    private LocalDate tripEndDate;

    private int viewCount;

    private int likeCount;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public Trip(Create requestDto, Member member) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.cost = requestDto.getCost();
        this.address = requestDto.getAddress();
        this.area = splitAddress(requestDto.getAddress());
        this.isPublic = requestDto.getIsPublic();
        this.placeName = requestDto.getPlaceName();
        this.tripStartDate = requestDto.getTripStartDate();
        this.tripEndDate = requestDto.getTripEndDate();
        this.viewCount = 0;
        this.likeCount = 0;
        this.member = member;
        this.isDeleted = false;
    }

    public void update(TripDto.Update requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.isPublic = requestDto.getIsPublic();
    }
    public void increaseViewCount() {
        this.viewCount++;
    }

    public void delete() {
        this.isDeleted = true;
    }

    private String splitAddress(String address) {
        if (address.isEmpty()) return "";

        return address.split(" ")[0];
    }
}

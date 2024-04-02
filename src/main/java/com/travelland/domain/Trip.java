package com.travelland.domain;

import com.travelland.dto.TripDto;
import com.travelland.dto.TripDto.CreateRequest;
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
    private String area;

    private boolean isPublic;

    private LocalDate tripStartDate;

    private LocalDate tripEndDate;

    private int viewCount;

    private int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public Trip(CreateRequest requestDto, Member member) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.cost = requestDto.getCost();
        this.area = requestDto.getArea();
        this.isPublic = requestDto.isPublic();
        this.tripStartDate = requestDto.getTripStartDate();
        this.tripEndDate = requestDto.getTripEndDate();
        this.viewCount = 0;
        this.likeCount = 0;
        this.member = member;
    }

    public void update(TripDto.UpdateRequest requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.cost = requestDto.getCost();
        this.area = requestDto.getArea();
        this.isPublic = requestDto.isPublic();
        this.tripStartDate = requestDto.getTripStartDate();
        this.tripEndDate = requestDto.getTripEndDate();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}

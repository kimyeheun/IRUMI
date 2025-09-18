package com.ssafy.pocketc_backend.domain.event.entity;

import com.ssafy.pocketc_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    @Column(nullable = false)
    private String eventName;

    @Column(length = 512)
    private String eventDescription;

    @Column(length = 512)
    private String badgeImageUrl;

    @Column(length = 512)
    private String eventImageUrl;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    private String badgeName;

    private String badgeDescription;
}
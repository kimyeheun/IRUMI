package com.ssafy.pocketc_backend.domain.event.entity;

import com.ssafy.pocketc_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
@ToString(exclude = "rooms")
public class Event extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    @Column(nullable = false, length = 255)
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

    @Column(length = 255)
    private String badgeName;

    @Column(length = 255)
    private String badgeDescription;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();
}
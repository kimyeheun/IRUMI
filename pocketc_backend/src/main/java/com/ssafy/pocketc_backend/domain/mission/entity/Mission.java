package com.ssafy.pocketc_backend.domain.mission.entity;

import com.ssafy.pocketc_backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "missions")
@ToString(exclude = "user")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer missionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer subId;

    @Column(length = 512)
    private String dsl;

    @Column(length = 512, nullable = false)
    private String mission;

    @Enumerated(EnumType.STRING)
    @Column(length = 512, nullable = false)
    private TimeTag timeTag = TimeTag.DAILY;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.IN_PROGRESS;
}
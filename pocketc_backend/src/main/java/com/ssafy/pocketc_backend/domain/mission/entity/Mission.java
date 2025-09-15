package com.ssafy.pocketc_backend.domain.mission.entity;

import com.ssafy.pocketc_backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
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

    @Column(length = 512)
    private String missionDsl;

    @Column(length = 512, nullable = false)
    private String mission;

    private Integer timeTag;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
}

enum Status {
    SUCCESS, IN_PROGRESS, FAILURE
}

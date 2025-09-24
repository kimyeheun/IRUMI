package com.ssafy.pocketc_backend.domain.user.entity;

import com.ssafy.pocketc_backend.domain.event.entity.Room;
import com.ssafy.pocketc_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(length = 20)
    private String name;

    @Column(length = 50)
    private String email;

    @Column(length = 60)
    private String password;

    @Column
    private String profileImageUrl;

    @Column
    private Long budget;

    @Column(nullable = false)
    @Builder.Default
    private Integer puzzleAttempts = 0;

    //    @Column
//    private String feedback;
    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateName(String name) {
        if (name != null) this.name = name;
    }

    public void updateBudget(Long budget) {
        if (budget != null) this.budget = budget;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
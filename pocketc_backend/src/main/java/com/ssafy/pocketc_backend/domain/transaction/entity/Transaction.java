package com.ssafy.pocketc_backend.domain.transaction.entity;

import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime transactedAt;
    private Long amount;

    @Column
    private String merchantName;

    private Integer majorId;
    private Integer subId;

    private boolean isApplied;
    private boolean isFixed;
}

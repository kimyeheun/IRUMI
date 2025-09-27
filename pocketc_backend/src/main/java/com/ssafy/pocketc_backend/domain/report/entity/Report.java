package com.ssafy.pocketc_backend.domain.report.entity;

import com.ssafy.pocketc_backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reports")
public class Report implements Comparable<Report> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate reportMonth;
    private Long monthlyTotalExpense;
    private Long monthlyFixedExpense;
    private Long monthlyBudget;

    @Override
    public int compareTo(Report other) {
        if (this.reportMonth == null && other.reportMonth == null) return 0;
        if (this.reportMonth == null) return 1;
        if (other.reportMonth == null) return -1;

        return this.reportMonth.compareTo(other.reportMonth);
    }
}
package com.ssafy.pocketc_backend.domain.report.repository;

import com.ssafy.pocketc_backend.domain.report.dto.response.ExpenseByCategoryDto;
import com.ssafy.pocketc_backend.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    Optional<Report> findByUser_UserIdAndReportMonth(Integer userId, LocalDate reportMonth);

    List<Report> findAllByUser_UserIdAndReportMonthBetweenOrderByReportMonthAsc(Integer userId, LocalDate start, LocalDate end);

    @Query("SELECT new com.ssafy.pocketc_backend.domain.report.dto.response.ExpenseByCategoryDto( " +
            "       t.majorId, SUM(t.amount)) " +
            "FROM Transaction t " +
            "WHERE t.user.userId = :userId " +
            "AND t.transactedAt >= :start " +
            "AND t.transactedAt < :end " +
            "GROUP BY t.majorId")

    List<ExpenseByCategoryDto> findExpenseByCategoryForMonth(
            @Param("userId") Integer userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}

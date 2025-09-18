package com.ssafy.pocketc_backend.domain.report.repository;

import com.ssafy.pocketc_backend.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report,Integer> {
    Optional<Report> findByUser_UserIdAndReportMonth(Integer userId, LocalDate reportMonth);
}

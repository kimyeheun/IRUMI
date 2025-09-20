package com.ssafy.pocketc_backend.domain.transaction.repository;

import com.ssafy.pocketc_backend.domain.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    List<Transaction> findAllByUser_UserIdAndTransactedAtGreaterThanEqualAndTransactedAtLessThan(Integer userId, LocalDateTime from, LocalDateTime to);
    List<Transaction> findAllByUser_UserIdAndTransactedAtBetween(Integer userId, LocalDate start, LocalDate end);
    List<Transaction> findAllByUser_UserIdAndMajorCategory(Integer userId, Integer majorCategory);
    List<Transaction> findAllByUser_UserIdAndSubCategory(Integer userId, Integer subCategory);
    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.user.userId = :userId " +
            "AND t.transactedAt BETWEEN :from AND :to " +
            "AND t.isApplied = true")
    int getTotalSpending(Integer userId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.user.userId = :userId " +
            "AND t.isFixed = true " +
            "AND t.transactedAt BETWEEN :from AND :to " +
            "AND t.isApplied = true")
    int getFixedSpending(Integer userId, LocalDateTime from, LocalDateTime to);

}

package com.ssafy.pocketc_backend.domain.transaction.repository;

import com.ssafy.pocketc_backend.domain.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    List<Transaction> findAllByUser_UserIdAndTransactedAtGreaterThanEqualAndTransactedAtLessThan(Integer userId, LocalDateTime from, LocalDateTime to);
    List<Transaction> findAllByUser_UserIdAndMajorCategory(Integer userId, Integer majorCategory);
    List<Transaction> findAllByUser_UserIdAndSubCategory(Integer userId, Integer subCategory);
}
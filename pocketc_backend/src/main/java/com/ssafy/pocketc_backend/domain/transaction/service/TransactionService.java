package com.ssafy.pocketc_backend.domain.transaction.service;

import com.ssafy.pocketc_backend.domain.report.service.ReportService;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.MonthReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionCreateReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionCreatedResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionListResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionResDto;
import com.ssafy.pocketc_backend.domain.transaction.entity.Transaction;
import com.ssafy.pocketc_backend.domain.transaction.repository.TransactionRepository;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static com.ssafy.pocketc_backend.domain.transaction.exception.TransactionErrorType.ERROR_GET_MONTHLY_TRANSACTIONS;
import static com.ssafy.pocketc_backend.domain.transaction.exception.TransactionErrorType.ERROR_GET_TRANSACTION;
import static com.ssafy.pocketc_backend.domain.user.exception.UserErrorType.NOT_FOUND_MEMBER_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final ReportService reportService;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionResDto getTransactionById(int transactionId, Integer userId) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        return TransactionResDto.from(transaction);
    }

    public TransactionListResDto getMonthlyTransactionList(MonthReqDto dto, Integer userId) {
        LocalDateTime from = dto.month().atDay(1).atStartOfDay();
        LocalDateTime to = dto.month().plusMonths(1).atDay(1).atStartOfDay();

        if (dto.month().isAfter(YearMonth.now())) throw new CustomException(ERROR_GET_MONTHLY_TRANSACTIONS);

        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndTransactedAtGreaterThanEqualAndTransactedAtLessThan(userId, from, to);

        return buildTransactionListDto(transactions);
    }

    public TransactionResDto updateTransaction(Integer transactionId, TransactionReqDto dto, Integer userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        LocalDate curMonth = dto.date().toLocalDate().withDayOfMonth(1);

        if (transaction.isFixed() && dto.isFixed()) {
            reportService.updateMonthlyFixedExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
            reportService.updateMonthlyTotalExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
        } else if (transaction.isFixed()) {
            reportService.updateMonthlyFixedExpense(userId, curMonth, -transaction.getAmount());
            reportService.updateMonthlyTotalExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
        } else if (dto.isFixed()) {
            reportService.updateMonthlyFixedExpense(userId, curMonth, dto.amount());
            reportService.updateMonthlyTotalExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
        } else {
            reportService.updateMonthlyTotalExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
        }

        transaction.setFixed(dto.isFixed());
        transaction.setAmount(dto.amount());
        transaction.setTransactedAt(dto.date());
        transaction.setMajorCategory(dto.majorCategory());
        transaction.setSubCategory(dto.subCategory());
        transaction.setMerchantName(dto.merchantName());

        return TransactionResDto.from(transaction);
    }

    public TransactionListResDto getMajorCategory(Integer majorCategory, Integer userId) {
        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndMajorCategory(userId, majorCategory);
        return buildTransactionListDto(transactions);
    }

    public TransactionListResDto getSubCategory(Integer subCategory, Integer userId) {
        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndSubCategory(userId, subCategory);
        return buildTransactionListDto(transactions);
    }

    public TransactionCreatedResDto createTransaction(Integer userId, TransactionCreateReqDto dto) {

        ////////////////////////// 더미 데이터 //////////////////////////
        /*TODO AI API 호출 : 카테고리 부여*/
        Transaction categorizedTransaction = new Transaction();
        categorizedTransaction.setMajorCategory(1);
        categorizedTransaction.setSubCategory(1);
        categorizedTransaction.setFixed(true);
        ////////////////////////// 더미 데이터 //////////////////////////

        Transaction transaction = new Transaction();
        transaction.setTransactedAt(LocalDateTime.now());
        transaction.setUser(userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR)));
        transaction.setAmount(dto.amount());
        transaction.setMerchantName(dto.merchantName());
        transaction.setMajorCategory(categorizedTransaction.getMajorCategory());
        transaction.setSubCategory(categorizedTransaction.getSubCategory());
        transaction.setFixed(categorizedTransaction.isFixed());

        LocalDate curMonth = dto.date().toLocalDate().withDayOfMonth(1);
        transactionRepository.save(transaction);
        if (transaction.isFixed()) {
            reportService.updateMonthlyFixedExpense(userId, curMonth, transaction.getAmount());
            reportService.updateMonthlyTotalExpense(userId, curMonth, transaction.getAmount());
        } else {
            reportService.updateMonthlyTotalExpense(userId, curMonth, transaction.getAmount());
        }

        return new TransactionCreatedResDto(transaction.getTransactionId(), transaction.getUser().getUserId());
    }

    private TransactionListResDto buildTransactionListDto(List<Transaction> transactions) {
        List<TransactionResDto> transactionResDtoList = new ArrayList<>();
        Long totalSpending = 0L;
        for (Transaction transaction : transactions) {
            transactionResDtoList.add(TransactionResDto.from(transaction));
            totalSpending += transaction.getAmount();
        }
        return TransactionListResDto.of(transactionResDtoList, totalSpending);
    }
}

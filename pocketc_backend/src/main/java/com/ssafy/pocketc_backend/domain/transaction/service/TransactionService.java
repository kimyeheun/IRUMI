package com.ssafy.pocketc_backend.domain.transaction.service;

import com.ssafy.pocketc_backend.domain.report.service.ReportService;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.MonthReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionListResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionResDto;
import com.ssafy.pocketc_backend.domain.transaction.entity.Transaction;
import com.ssafy.pocketc_backend.domain.transaction.repository.TransactionRepository;
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

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final ReportService reportService;
    private final TransactionRepository transactionRepository;

    public TransactionResDto getTransactionById(int transactionId, Principal principal) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        return TransactionResDto.from(transaction);
    }

    public TransactionListResDto getMonthlyTransactionList(MonthReqDto dto, Principal principal) {
        int userId = 1;
        LocalDateTime from = dto.month().atDay(1).atStartOfDay();
        LocalDateTime to = dto.month().plusMonths(1).atDay(1).atStartOfDay();

        if (dto.month().isAfter(YearMonth.now())) throw new CustomException(ERROR_GET_MONTHLY_TRANSACTIONS);

        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndTransactedAtGreaterThanEqualAndTransactedAtLessThan(userId, from, to);

        return buildTransactionListDto(transactions);
    }

    public TransactionResDto updateTransaction(Integer transactionId, TransactionReqDto dto, Principal principal) {
        int userId = 1;
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        LocalDate curMonth = dto.date().toLocalDate().withDayOfMonth(1);

        if (transaction.isFixed() && dto.isFixed()) {
            reportService.updateFixedExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
            reportService.updateTotalExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
        } else if (transaction.isFixed()) {
            reportService.updateFixedExpense(userId, curMonth, -transaction.getAmount());
            reportService.updateTotalExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
        } else if (dto.isFixed()) {
            reportService.updateFixedExpense(userId, curMonth, dto.amount());
            reportService.updateTotalExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
        } else {
            reportService.updateTotalExpense(userId, curMonth, -transaction.getAmount() + dto.amount());
        }

        transaction.setFixed(dto.isFixed());
        transaction.setAmount(dto.amount());
        transaction.setTransactedAt(dto.date());
        transaction.setMajorCategory(dto.majorCategory());
        transaction.setSubCategory(dto.subCategory());
        transaction.setMerchantName(dto.merchantName());

        return TransactionResDto.from(transaction);
    }

    public TransactionListResDto getMajorCategory(Integer majorCategory, Principal principal) {
        int userId = 1;
        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndMajorCategory(userId, majorCategory);
        return buildTransactionListDto(transactions);
    }

    public TransactionListResDto getSubCategory(Integer subCategory, Principal principal) {
        int userId = 1;
        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndSubCategory(userId, subCategory);
        return buildTransactionListDto(transactions);
    }

    private TransactionListResDto buildTransactionListDto(List<Transaction> transactions) {
        List<TransactionResDto> transactionResDtoList = new ArrayList<>();
        int totalSpending = 0;
        for (Transaction transaction : transactions) {
            transactionResDtoList.add(TransactionResDto.from(transaction));
            totalSpending += transaction.getAmount();
        }
        return TransactionListResDto.of(transactionResDtoList, totalSpending);
    }
}

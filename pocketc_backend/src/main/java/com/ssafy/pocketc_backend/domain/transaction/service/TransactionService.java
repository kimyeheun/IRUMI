package com.ssafy.pocketc_backend.domain.transaction.service;

import com.ssafy.pocketc_backend.domain.transaction.dto.request.MonthReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionListResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionResDto;
import com.ssafy.pocketc_backend.domain.transaction.entity.Transaction;
import com.ssafy.pocketc_backend.domain.transaction.repository.TransactionRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
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

        List<Transaction> transactionList = transactionRepository.findAllByUser_UserIdAndTransactedAtGreaterThanEqualAndTransactedAtLessThan(userId, from, to);

        List<TransactionResDto> transactionResDtoList = new ArrayList<>();
        int totalSpending = 0;
        for (Transaction transaction : transactionList) {
            transactionResDtoList.add(TransactionResDto.from(transaction));
            totalSpending += transaction.getAmount();
        }
        return TransactionListResDto.of(dto.month(), transactionResDtoList, totalSpending);
    }
}

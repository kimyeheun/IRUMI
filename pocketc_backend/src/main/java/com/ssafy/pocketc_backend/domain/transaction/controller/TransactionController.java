package com.ssafy.pocketc_backend.domain.transaction.controller;

import com.ssafy.pocketc_backend.domain.transaction.dto.request.MonthReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionListResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionResDto;
import com.ssafy.pocketc_backend.domain.transaction.service.TransactionService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.transaction.exception.TransactionSuccessType.SUCCESS_GET_MONTHLY_TRANSACTIONS;
import static com.ssafy.pocketc_backend.domain.transaction.exception.TransactionSuccessType.SUCCESS_GET_TRANSACTION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResDto>> getTransaction(@PathVariable("transactionId") Integer transactionId, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
            SUCCESS_GET_TRANSACTION,
            transactionService.getTransactionById(transactionId, principal)
        ));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<TransactionListResDto>> getTransactions(@RequestBody MonthReqDto dto, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MONTHLY_TRANSACTIONS,
                transactionService.getMonthlyTransactionList(dto, principal)
        ));
    }
}
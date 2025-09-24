package com.ssafy.pocketc_backend.domain.transaction.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.MonthReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionCreateReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionCreatedResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionListResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionResDto;
import com.ssafy.pocketc_backend.domain.transaction.service.TransactionService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.transaction.exception.TransactionSuccessType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TransactionController {

    private final TransactionService transactionService;

    private Integer userId(Principal principal) { return Integer.parseInt(principal.getName()); }

    @GetMapping("/users/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResDto>> getTransaction(@PathVariable("transactionId") Integer transactionId) {
        return ResponseEntity.ok(ApiResponse.success(
            SUCCESS_GET_TRANSACTION,
            transactionService.getTransactionById(transactionId)
        ));
    }

    @GetMapping("/users/transactions")
    public ResponseEntity<ApiResponse<TransactionListResDto>> getTransactions(@RequestBody MonthReqDto dto, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MONTHLY_TRANSACTIONS,
                transactionService.getMonthlyTransactionList(dto, userId(principal))
        ));
    }

    @PutMapping("/users/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResDto>> updateTransaction(@PathVariable("transactionId") Integer transactionId, @RequestBody TransactionReqDto dto, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_UPDATE_TRANSACTION,
                transactionService.updateTransaction(transactionId, dto, userId(principal))
        ));
    }

    @GetMapping("/users/transactions/majorcategory/{majorCategory}")
    public ResponseEntity<ApiResponse<TransactionListResDto>> getMajorCategoryTransactions(@PathVariable("majorCategory") Integer majorCategory, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MAJOR_CATEGORY_TRANSACTIONS,
                transactionService.getMajorCategory(majorCategory, userId(principal))
        ));
    }

    @GetMapping("/users/transactions/subcategory/{subCategory}")
    public ResponseEntity<ApiResponse<TransactionListResDto>> getSubCategoryTransactions(@PathVariable("subCategory") Integer subCategory, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_SUB_CATEGORY_TRANSACTIONS,
                transactionService.getSubCategory(subCategory, userId(principal))
        ));
    }

    @PostMapping("/admin/transactions")
    public ResponseEntity<ApiResponse<TransactionCreatedResDto>> createTransaction(@RequestBody TransactionCreateReqDto dto) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CREATE_TRANSACTIONS,
                transactionService.createTransaction(dto)
        ));
    }

    @PostMapping("/users/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<?>> appliedTransaction(@PathVariable("transactionId") Integer transactionId, Principal principal) throws JsonProcessingException {
        transactionService.appliedTransaction(transactionId, userId(principal));
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_APPLIED_TRANSACTION
        ));
    }
}
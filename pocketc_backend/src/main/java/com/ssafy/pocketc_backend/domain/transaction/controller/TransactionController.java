package com.ssafy.pocketc_backend.domain.transaction.controller;

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

    @PutMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResDto>> updateTransaction(@PathVariable("transactionId") Integer transactionId, @RequestBody TransactionReqDto dto, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_UPDATE_TRANSACTION,
                transactionService.updateTransaction(transactionId, dto, principal)
        ));
    }

    @GetMapping("/transactions/majorcategory/{majorCategory}")
    public ResponseEntity<ApiResponse<TransactionListResDto>> getMajorCategoryTransactions(@PathVariable("majorCategory") Integer majorCategory, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MAJOR_CATEGORY_TRANSACTIONS,
                transactionService.getMajorCategory(majorCategory, principal)
        ));
    }

    @GetMapping("/transactions/subcategory/{subCategory}")
    public ResponseEntity<ApiResponse<TransactionListResDto>> getSubCategoryTransactions(@PathVariable("subCategory") Integer subCategory, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_SUB_CATEGORY_TRANSACTIONS,
                transactionService.getSubCategory(subCategory, principal)
        ));
    }

    @PostMapping("/{userId}/transactions")
    public ResponseEntity<ApiResponse<?>> createTransaction(@PathVariable Integer userId, @RequestBody TransactionCreateReqDto dto) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CREATE_TRANSACTIONS,
                transactionService.createTransaction(userId, dto)
        ));
    }
}
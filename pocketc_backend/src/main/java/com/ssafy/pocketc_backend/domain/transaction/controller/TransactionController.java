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

    private Integer userId(Principal principal) { return Integer.parseInt(principal.getName()); }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResDto>> getTransaction(@PathVariable("transactionId") Integer transactionId, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
            SUCCESS_GET_TRANSACTION,
            transactionService.getTransactionById(transactionId, userId(principal))
        ));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<TransactionListResDto>> getTransactions(@RequestBody MonthReqDto dto, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MONTHLY_TRANSACTIONS,
                transactionService.getMonthlyTransactionList(dto.month().atDay(1), userId(principal))
        ));
    }

    @PutMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResDto>> updateTransaction(@PathVariable("transactionId") Integer transactionId, @RequestBody TransactionReqDto dto, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_UPDATE_TRANSACTION,
                transactionService.updateTransaction(transactionId, dto, userId(principal))
        ));
    }

    @GetMapping("/transactions/majorcategory/{majorCategory}")
    public ResponseEntity<ApiResponse<TransactionListResDto>> getMajorCategoryTransactions(@PathVariable("majorCategory") Integer majorCategory, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MAJOR_CATEGORY_TRANSACTIONS,
                transactionService.getMajorCategory(majorCategory, userId(principal))
        ));
    }

    @GetMapping("/transactions/subcategory/{subCategory}")
    public ResponseEntity<ApiResponse<TransactionListResDto>> getSubCategoryTransactions(@PathVariable("subCategory") Integer subCategory, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_SUB_CATEGORY_TRANSACTIONS,
                transactionService.getSubCategory(subCategory, userId(principal))
        ));
    }

    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<?>> createTransaction(Principal principal, @RequestBody TransactionCreateReqDto dto) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CREATE_TRANSACTIONS,
                transactionService.createTransaction(userId(principal), dto)
        ));
    }
}

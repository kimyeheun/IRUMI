package com.ssafy.pocketc_backend.domain.transaction.service;

import com.ssafy.pocketc_backend.domain.report.service.ReportService;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionAiReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionCreateReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionAiResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionCreatedResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionListResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionResDto;
import com.ssafy.pocketc_backend.domain.transaction.entity.Transaction;
import com.ssafy.pocketc_backend.domain.transaction.repository.TransactionRepository;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

    private final WebClient transactionAiClient;

    // 여기서 userId를 사용하고 있지는 않은거 같은데
    public TransactionResDto getTransactionById(int transactionId, Integer userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        return TransactionResDto.from(transaction);
    }
    // 현재 달과 일치하는 결제내역을 조회한다.
    public TransactionListResDto getMonthlyTransactionList(LocalDate now, Integer userId) {
        // 입력된 시간이 현재 시간보다 미래 시간이라면 ERROR를 반환합니다.
        YearMonth yearMonth = YearMonth.from(now);
        if (yearMonth.isAfter(YearMonth.now())) throw new CustomException(ERROR_GET_MONTHLY_TRANSACTIONS);

        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndTransactedAtBetween(
                userId,
                now.withDayOfMonth(1),
                now.withDayOfMonth(now.lengthOfMonth())
        );

        return buildTransactionListDto(transactions);
    }

    public TransactionResDto updateTransaction(Integer transactionId, TransactionReqDto dto, int userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        LocalDate curMonth = transaction.getTransactedAt().toLocalDate().withDayOfMonth(1);

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
        transaction.setMajorCategory(dto.majorId());
        transaction.setSubCategory(dto.subId());
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

        TransactionAiReqDto transactionAiReqDto = TransactionAiReqDto.of(String.valueOf(dto.date()), dto.amount(), dto.merchantName());

        TransactionAiResDto categorizedTransaction = transactionAiClient.post()
                .uri("/ai/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionAiReqDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("AI categorize API error")
                                .flatMap(msg -> Mono.error(new RuntimeException(msg)))
                )
                .bodyToMono(TransactionAiResDto.class)
                .timeout(Duration.ofSeconds(3)).block();

        Transaction transaction = new Transaction();
        transaction.setTransactedAt(categorizedTransaction.transactedAt());
        transaction.setUser(userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR)));
        transaction.setAmount(categorizedTransaction.amount());
        transaction.setMerchantName(categorizedTransaction.merchantName());
        transaction.setSubCategory(categorizedTransaction.subId());
        transaction.setMajorCategory(categorizedTransaction.majorId());
        transaction.setFixed(categorizedTransaction.isFixed());

        LocalDate curMonth = categorizedTransaction.transactedAt().toLocalDate().withDayOfMonth(1);
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

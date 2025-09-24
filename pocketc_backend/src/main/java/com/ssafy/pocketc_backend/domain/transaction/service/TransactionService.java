package com.ssafy.pocketc_backend.domain.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionRedisDto;
import com.ssafy.pocketc_backend.domain.report.service.ReportService;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.MonthReqDto;
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

import java.time.*;
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

    private final ObjectMapper objectMapper;

    // 여기서 userId를 사용하고 있지는 않은거 같은데
    public TransactionResDto getTransactionById(int transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        return TransactionResDto.from(transaction);
    }

    public TransactionListResDto getMonthlyTransactionList(MonthReqDto dto, Integer userId) {
        YearMonth yearMonth = YearMonth.of(dto.year(), dto.month());

        if (yearMonth.isAfter(YearMonth.now())) {
            throw new CustomException(ERROR_GET_MONTHLY_TRANSACTIONS);
        }

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndTransactedAtGreaterThanEqualAndTransactedAtLessThan(
                userId,
                start.atStartOfDay(),
                end.plusDays(1).atStartOfDay()
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
        transaction.setMajorId(dto.majorId());
        transaction.setSubId(dto.subId());
        transaction.setMerchantName(dto.merchantName());

        return TransactionResDto.from(transaction);
    }

    public TransactionListResDto getMajorCategory(Integer majorCategory, Integer userId) {
        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndMajorId(userId, majorCategory);
        return buildTransactionListDto(transactions);
    }

    public TransactionListResDto getSubCategory(Integer subCategory, Integer userId) {
        List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndSubId(userId, subCategory);
        return buildTransactionListDto(transactions);
    }

    public TransactionCreatedResDto createTransaction(TransactionCreateReqDto dto) {

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

        Integer userId = dto.userId();

        Transaction transaction = new Transaction();
        transaction.setTransactedAt(categorizedTransaction.transactedAt());
        transaction.setUser(userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR)));
        transaction.setAmount(categorizedTransaction.amount());
        transaction.setMerchantName(categorizedTransaction.merchantName());
        transaction.setSubId(categorizedTransaction.subId());
        transaction.setMajorId(categorizedTransaction.majorId());
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

     public boolean checkMissionByTransaction(Integer transactionId, MissionRedisDto mission) throws JsonProcessingException {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        String json = mission.getDsl();

        JsonNode root = objectMapper.readTree(json);

        String template = root.get("template").toString();
        int subId = root.get("sub_id").asInt();
        int value = root.get("value").asInt();

        if (subId != transaction.getSubId()) return true;

        LocalDateTime start = null, end = null;
        if (template.equals("TIME_BAN_DAILY")) {
            JsonNode tod = root.get("time_of_day");
            JsonNode tr = tod.get(0);
            String startStr = tr.get("start").asText();
            String endStr   = tr.get("end").asText();

            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            LocalTime st = LocalTime.parse(startStr);
            LocalTime en = LocalTime.parse(endStr);

            start = LocalDateTime.of(today, st);
            end   = LocalDateTime.of(today, en);

            if (end.isBefore(start)) {
                end = end.plusDays(1);
            }
        }

        int dayOfWeek = 0;
        if (template.equals("DAY_BAN_WEEKLY")) {
            dayOfWeek = root.get("day_of_week").asInt();
        }

        int progress = mission.getProgress();

        long amount = transaction.getAmount();
        LocalDateTime transactedAt = transaction.getTransactedAt();

        boolean check = true;
        switch (template) {
            case "CATEGORY_BAN_DAILY":
                check = false;
                break;
            case "SPEND_CAP_DAILY":
                if (progress + amount > value)
                    check = false;
                break;
            case "PER_TXN_DAILY":
                if (amount > value)
                    check = false;
                break;
            case "TIME_BAN_DAILY":
                if (start.isAfter(transactedAt) && end.isBefore(transactedAt))
                    check = false;
                break;
            case "COUNT_CAP_DAILY":
                if (progress + 1 > value)
                    check = false;
                break;
            case "DAY_BAN_WEEKLY":
                if (dayOfWeek == (LocalDate.now().getDayOfWeek().getValue() % 7))
                    check = false;
                break;
            case "SPEND_CAP_WEEKLY":
                if (progress + amount > value) check = false;
                break;
            case "COUNT_CAP_WEEKLY":
                if (progress + 1 > value) check = false;
                break;
            case "SPEND_CAP_MONTHLY":
                if (progress + amount > value) check = false;
                break;
            case "COUNT_CAP_MONTHLY":
                if (progress + 1 > value) check = false;
                break;
        }
        return check;
    }
}
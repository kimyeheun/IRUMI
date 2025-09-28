package com.ssafy.pocketc_backend.domain.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.pocketc_backend.domain.event.entity.Status;
import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionRedisDto;
import com.ssafy.pocketc_backend.domain.mission.dto.response.MissionInfoDto;
import com.ssafy.pocketc_backend.domain.mission.service.MissionRedisService;
import com.ssafy.pocketc_backend.domain.report.entity.Report;
import com.ssafy.pocketc_backend.domain.report.repository.ReportRepository;
import com.ssafy.pocketc_backend.domain.report.service.ReportService;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.Dummy;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionAiReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionCreateReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.TransactionReqDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionAiResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionCreatedResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionListResDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionResDto;
import com.ssafy.pocketc_backend.domain.transaction.entity.Transaction;
import com.ssafy.pocketc_backend.domain.transaction.repository.TransactionRepository;
import com.ssafy.pocketc_backend.domain.user.entity.Streak;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.StreakRepository;
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
import java.util.Objects;
import java.util.Optional;

import static com.ssafy.pocketc_backend.domain.transaction.exception.TransactionErrorType.*;
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

    private final MissionRedisService missionRedisService;
    private final StreakRepository streakRepository;
    private final ReportRepository reportRepository;

    public TransactionResDto getTransactionById(int transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        return TransactionResDto.from(transaction);
    }

    public TransactionListResDto getMonthlyTransactionList(Integer year, Integer month, Integer userId) {
        YearMonth yearMonth = YearMonth.of(year, month);

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

        Streak streak = streakRepository.findByUser_userIdAndDate(userId, transaction.getTransactedAt().toLocalDate());
        streak.setSpentAmount(streak.getSpentAmount() - transaction.getAmount() + dto.amount());

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

        Streak streak = streakRepository.findByUser_userIdAndDate(dto.userId(), dto.date().toLocalDate());
        streak.setSpentAmount(streak.getSpentAmount() + dto.amount());

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
        transaction.setMerchantName(dto.merchantName());
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

    public int appliedTransaction(Integer transactionId, Integer userId) throws JsonProcessingException {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        if (transaction.isApplied()) throw new CustomException(ERROR_ALREADY_APPLIED);
        transaction.setApplied(true);

        LocalDate date = LocalDate.from(transaction.getTransactedAt());
        if (transaction.getTransactedAt().toLocalTime().isBefore(LocalTime.of(6, 0))) {
            date = date.minusDays(1);
        }

        String key = missionRedisService.buildKey(userId, date);

        List<MissionRedisDto> cached = Optional.ofNullable(missionRedisService.getList(key))
                .orElse(List.of());

        int count = 0;
        List<MissionRedisDto> newCached = new ArrayList<>();
        for (MissionRedisDto missionRedisDto : cached) {
            if (missionRedisDto.getStatus() != Status.IN_PROGRESS) {
                newCached.add(missionRedisDto);
                continue;
            }
            MissionInfoDto missionInfo = checkMissionByTransaction(transaction, missionRedisDto);
            if (!missionInfo.check()) {
                missionRedisDto.setStatus(Status.FAILURE);
                count++;
            }
            missionRedisDto.setProgress(missionInfo.progress());
            newCached.add(missionRedisDto);
        }
        missionRedisService.putList(key, newCached, missionRedisService.ttlUntilNext6am());
        return cached.size() - count;
    }

    public MissionInfoDto checkMissionByTransaction(Transaction transaction, MissionRedisDto mission) throws JsonProcessingException {

        String json = mission.getDsl().replace("'", "\"");

        JsonNode root = objectMapper.readTree(json);

        String template = root.get("template").toString().replace("\"", "");
        int subId = root.get("sub_id").asInt();
        long value = root.get("value").asLong();
        long progress = mission.getProgress();

        if (subId != transaction.getSubId()) return MissionInfoDto.of(true, progress, value, template);

        LocalDateTime start = null, end = null;
        if (template.equals("TIME_BAN_DAILY")) {
            JsonNode tod = root.get("time_of_day");
            JsonNode tr = tod.get(0);
            String startStr = tr.get("start").asText();
            String endStr = tr.get("end").asText();

            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            LocalTime st = LocalTime.parse(startStr);
            LocalTime en = LocalTime.parse(endStr);

            start = LocalDateTime.of(today, st);
            end = LocalDateTime.of(today, en);

            if (end.isBefore(start)) {
                end = end.plusDays(1);
            }
        }

        int dayOfWeek = 0;
        if (template.equals("DAY_BAN_WEEKLY")) {
            dayOfWeek = root.get("day_of_week").asInt();
        }

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
                progress += amount;
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
                progress += 1;
                break;
            case "DAY_BAN_WEEKLY":
                if (dayOfWeek == (LocalDate.now().getDayOfWeek().getValue() % 7))
                    check = false;
                break;
            case "SPEND_CAP_WEEKLY":
                if (progress + amount > value) check = false;
                progress += amount;
                break;
            case "COUNT_CAP_WEEKLY":
                if (progress + 1 > value) check = false;
                progress += 1;
                break;
            case "SPEND_CAP_MONTHLY":
                if (progress + amount > value) check = false;
                progress += amount;
                break;
            case "COUNT_CAP_MONTHLY":
                if (progress + 1 > value) check = false;
                progress += 1;
                break;
        }
        return MissionInfoDto.of(check, progress, value, template);
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

//    public void createTransactions(Integer userId, DummyTransactionsDto dto) {
//        Random random = new Random();
//        List<Dummy> dtos = dto.transactions();
//
//        User user = userRepository.findById(userId)
//                .orElseThrow();
//        for (Dummy transaction : dtos) {
//
//            long amount = transaction.amount() / 100 * 100;
//
//            Streak streak = streakRepository.findByUser_userIdAndDate(userId, transaction.transactedAt().toLocalDate());
//            streak.setSpentAmount(streak.getSpentAmount() + amount);
//
//            Transaction t = Transaction.builder()
//                    .user(user)
//                    .amount(amount)
//                    .transactedAt(transaction.transactedAt())
//                    .isApplied(false)
//                    .isFixed(random.nextBoolean())
//                    .merchantName(transaction.merchantName())
//                    .majorId(transaction.majorId())
//                    .subId(transaction.subId())
//                    .build();
//            LocalDate curMonth = transaction.transactedAt().toLocalDate().withDayOfMonth(1);
//            transactionRepository.save(t);
//            if (t.isFixed()) {
//                reportService.updateMonthlyFixedExpense(userId, curMonth, amount);
//                reportService.updateMonthlyTotalExpense(userId, curMonth, amount);
//            } else {
//                reportService.updateMonthlyTotalExpense(userId, curMonth, amount);
//            }
//        }
//    }

    public void putTransaction(Dummy dummy, User user) {
        Transaction transaction = transactionRepository.findById(dummy.getTransactionId())
                .orElseThrow(() -> new CustomException(ERROR_GET_TRANSACTION));

        transaction.setFixed(dummy.isFixed());
        transaction.setTransactedAt(dummy.getTransactedAt());
        transaction.setMajorId(dummy.getMajorId());
        transaction.setSubId(dummy.getSubId());
        if (Objects.equals(transaction.getTransactedAt().toLocalDate(), LocalDate.now()))
            transaction.setApplied(false);

        transactionRepository.save(transaction);

        Optional<Report> report = reportRepository.findByUser_UserIdAndReportMonth(user.getUserId(), transaction.getTransactedAt().toLocalDate().withDayOfMonth(1));
        if (report.isEmpty()) {
            Report newReport = Report.builder()
                    .monthlyBudget(user.getBudget())
                    .monthlyFixedExpense(0L)
                    .monthlyTotalExpense(0L)
                    .reportMonth(transaction.getTransactedAt().toLocalDate().withDayOfMonth(1))
                    .user(user)
                    .build();
            report = Optional.of(reportRepository.save(newReport));
        }
        if (dummy.isFixed()) {
            report.get().setMonthlyFixedExpense(report.get().getMonthlyFixedExpense() + dummy.getAmount());
        }
        report.get().setMonthlyTotalExpense(report.get().getMonthlyTotalExpense() + dummy.getAmount());
    }
}
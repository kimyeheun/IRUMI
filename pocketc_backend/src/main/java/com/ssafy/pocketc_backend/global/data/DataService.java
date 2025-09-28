package com.ssafy.pocketc_backend.global.data;

import com.ssafy.pocketc_backend.domain.event.entity.Event;
import com.ssafy.pocketc_backend.domain.event.repository.EventRepository;
import com.ssafy.pocketc_backend.domain.mission.service.MissionRedisService;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.Dummy;
import com.ssafy.pocketc_backend.domain.transaction.dto.request.DummyTransactionsDto;
import com.ssafy.pocketc_backend.domain.transaction.dto.response.TransactionAiResDto;
import com.ssafy.pocketc_backend.domain.transaction.service.TransactionService;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

import static com.ssafy.pocketc_backend.domain.user.exception.UserErrorType.NOT_FOUND_MEMBER_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class DataService {

    private final EventRepository eventRepository;

    private final MissionRedisService missionRedisService;

    private final DataSource dataSource;
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TransactionService transactionService;
    private final ResourceLoader resourceLoader;

    private final WebClient webClient;
    private final UserRepository userRepository;

    public void deleteAll() {

        jdbc.execute("SET FOREIGN_KEY_CHECKS=0");

        for (String t : new String[]{
                "streaks","transactions","follows","missions", "user_metrics",
                "reports","badges","rooms","events","users","puzzles"
        }) {
            jdbc.execute("TRUNCATE TABLE " + t);
        }
        jdbc.execute("SET FOREIGN_KEY_CHECKS=1");
        missionRedisService.deleteAll();

        missionRedisService.deleteAll();
    }

    public void deleteLastEvent() {
        Event firstByOrderByEventIdDesc = eventRepository.findFirstByOrderByEventIdDesc();
        eventRepository.delete(firstByOrderByEventIdDesc);
    }

    public void putAllData() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/init_v2.sql"));
        populator.setContinueOnError(false);
        populator.setSeparator(";");
        populator.execute(dataSource);
    }

    public void putDummyTransactions(Integer userId) throws IOException {

        Resource res = new ClassPathResource("db/init_v3.sql");
        if (!res.exists()) throw new IllegalStateException("db/init_v3.sql not found");

        String sql = new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        sql = sql.replaceAll("(?s)/\\*.*?\\*/", "").trim();

        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);

        jdbcTemplate.update(sql, params);
    }

    public void getDummyTransactions(Integer userId) throws IOException {
        // AI로 호출
        DummyTransactionsDto dummyTransactionsDto = webClient.post()
                .uri("/ai/categories/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("AI categorize API error")
                                .flatMap(msg -> Mono.error(new RuntimeException(msg)))
                )
                .bodyToMono(DummyTransactionsDto.class)
                .timeout(Duration.ofSeconds(10)).block();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        for (Dummy dummy : dummyTransactionsDto.getTransactions()) {
            System.out.println(dummy.getMerchantName() + ": " + dummy.getMajorId() + " " + dummy.getSubId());
            transactionService.putTransaction(dummy, user);
        }
    }
}
package com.ssafy.pocketc_backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient missionWebClient(@Value("${ai.base-url}") String baseUrlRaw) {
        String baseUrl = (baseUrlRaw == null ? "" : baseUrlRaw.trim());

        // 스킴 없으면 복구 (디펜시브)
        if (!baseUrl.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) {
            System.err.println("[WARN] ai.base-url has no scheme. Prepending http:// : " + baseUrl);
            baseUrl = "http://" + baseUrl;
        }
        // 최종 검증 (여기서 터지면 설정값 문제 확정)
        java.net.URI.create(baseUrl);

        HttpClient httpClient = HttpClient.create()
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) // connect 3s
                .responseTimeout(Duration.ofSeconds(10))                             // total response 10s
                .doOnConnected(conn -> conn
                        .addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(10))
                        .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(10)))
                .wiretap(true); // 네트워크 로깅

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter((req, next) -> { // 전역 요청/응답 로그
                    System.out.println("[WebClient] => " + req.method() + " " + req.url());
                    return next.exchange(req).doOnNext(res ->
                            System.out.println("[WebClient] <= " + res.statusCode()));
                })
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
                        .build())
                .build();
    }
}

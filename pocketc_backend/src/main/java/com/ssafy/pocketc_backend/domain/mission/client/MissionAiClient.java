package com.ssafy.pocketc_backend.domain.mission.client;

import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MissionAiClient {
    private final WebClient missionWebClient;

    public Mono<MissionApiResponse> getDailyMissions(Integer userId) {
        if (userId == null) return Mono.error(new IllegalArgumentException("userId null"));
        return missionWebClient.get()
                .uri("/ai/missions/{userId}/daily", userId)  // 안전한 템플릿
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MissionApiResponse.class);
    }

    public Mono<MissionApiResponse> getWeeklyMissions(Integer userId) {
        if (userId == null) return Mono.error(new IllegalArgumentException("userId null"));
        return missionWebClient.get()
                .uri("/ai/missions/{userId}/weekly", userId)  // 안전한 템플릿
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MissionApiResponse.class);
    }

    public Mono<MissionApiResponse> getMonthlyMissions(Integer userId) {
        if (userId == null) return Mono.error(new IllegalArgumentException("userId null"));
        return missionWebClient.get()
                .uri("/ai/missions/{userId}/monthly", userId)  // 안전한 템플릿
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MissionApiResponse.class);
    }
}
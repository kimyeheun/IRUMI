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
        return missionWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ai/missions/{userId}/daily")
                        .build(userId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MissionApiResponse.class);
    }

    public Mono<MissionApiResponse> getWeeklyMissions(Integer userId) {
        return missionWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ai/missions/{userId}/weekly")
                        .build(userId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MissionApiResponse.class);
    }

    public Mono<MissionApiResponse> getMonthlyMissions(Integer userId) {
        return missionWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ai/missions/{userId}/monthly")
                        .build(userId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MissionApiResponse.class);
    }
}
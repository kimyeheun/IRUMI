package com.ssafy.pocketc_backend.domain.mission.dto.request;

import com.ssafy.pocketc_backend.domain.event.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionRedisDto implements Serializable {
    private Integer subId;
    private String dsl;
    private String mission;
    private Integer type;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Status status = Status.IN_PROGRESS;
    private Integer progress;
}
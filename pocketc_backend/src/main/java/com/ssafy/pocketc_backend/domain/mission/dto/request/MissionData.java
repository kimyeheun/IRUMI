package com.ssafy.pocketc_backend.domain.mission.dto.request;

import java.time.LocalDate;
import java.util.List;

public record MissionData(
      Integer userId,
      LocalDate date,
      List<MissionItem> missions
) {}

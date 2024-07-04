package ru.practicum.stats.service;

import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.dto.HitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    HitDto saveHit(HitDto hitDto);

    List<StatsDto> getStats(LocalDateTime startDate, LocalDateTime endDate, String[] uris, Boolean unique);
}

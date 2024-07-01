package ru.practicum.stats.service;

import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.dto.HitDto;

public interface StatsService {
    HitDto saveHit(HitDto hitDto);

    StatsDto[] getStats(String start, String end, String[] uris, Boolean unique);
}

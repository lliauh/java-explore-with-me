package ru.practicum.explorewithme.stats.service;

import ru.practicum.explorewithme.stats.dto.StatsDto;
import ru.practicum.explorewithme.stats.dto.HitDto;

public interface StatsService {
    HitDto saveHit(HitDto hitDto);

    StatsDto[] getStats(String start, String end, String[] uris, Boolean unique);
}

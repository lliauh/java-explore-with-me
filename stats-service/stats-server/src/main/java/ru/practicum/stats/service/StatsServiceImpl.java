package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.HitMapper;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final HitRepository hitRepository;

    @Override
    public HitDto saveHit(HitDto hitDto) {
        Hit hit = HitMapper.toHit(hitDto);

        return HitMapper.toHitDto(hitRepository.save(hit));
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        if (uris == null) {
            List<StatsDto> allStats;

            if (unique) {
                allStats = hitRepository.getAllStatsUnique(start, end);
            } else {
                allStats = hitRepository.getAllStats(start, end);
            }

            return allStats;
        }

        List<StatsDto> stats = new ArrayList<>();

        for (String uri : uris) {
            if (unique) {
                stats.add(hitRepository.getStatsByUriUnique(uri, start, end));
            } else {
                stats.add(hitRepository.getStatsByUri(uri, start, end));
            }
        }

        stats.sort(Collections.reverseOrder(Comparator.comparing(StatsDto::getHits)));

        return stats;
    }
}

package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.HitMapper;
import ru.practicum.stats.repository.HitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
    public StatsDto[] getStats(String start, String end, String[] uris, Boolean unique) {
        LocalDateTime decodedStart = decodeDateFromParam(start);
        LocalDateTime decodedEnd = decodeDateFromParam(end);

        if (uris == null) {
            List<StatsDto> allStats;

            if (unique) {
                allStats = hitRepository.getAllStatsUnique(decodedStart, decodedEnd);
            } else {
                allStats = hitRepository.getAllStats(decodedStart, decodedEnd);
            }

            StatsDto[] statsArray = new StatsDto[allStats.size()];
            allStats.toArray(statsArray);

            return statsArray;
        }

        StatsDto[] stats = new StatsDto[uris.length];

        for (int i = 0; i < uris.length; i++) {
            if (unique) {
                stats[i] = hitRepository.getStatsByUriUnique(uris[i], decodedStart, decodedEnd);
            } else {
                stats[i] = hitRepository.getStatsByUri(uris[i], decodedStart, decodedEnd);
            }
        }

        Arrays.sort(stats, Collections.reverseOrder(Comparator.comparing(StatsDto::getHits)));

        return stats;
    }

    private LocalDateTime decodeDateFromParam(String date) {
        return LocalDateTime.parse(URLDecoder.decode(date, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

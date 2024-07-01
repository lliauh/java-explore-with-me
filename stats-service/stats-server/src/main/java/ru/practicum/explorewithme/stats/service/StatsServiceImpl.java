package ru.practicum.explorewithme.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.stats.dto.HitDto;
import ru.practicum.explorewithme.stats.dto.StatsDto;
import ru.practicum.explorewithme.stats.model.Hit;
import ru.practicum.explorewithme.stats.model.HitMapper;
import ru.practicum.explorewithme.stats.repository.HitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        StatsDto[] stats = new StatsDto[uris.length];

        for (int i = 0; i < uris.length; i++) {
            if (unique) {
                stats[i] = hitRepository.getStatsByUriUnique(uris[i], decodedStart, decodedEnd);
            } else {
                stats[i] = hitRepository.getStatsByUri(uris[i], decodedStart, decodedEnd);
            }
        }

        return stats;
    }

    private LocalDateTime decodeDateFromParam(String date) {
        return LocalDateTime.parse(URLDecoder.decode(date, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

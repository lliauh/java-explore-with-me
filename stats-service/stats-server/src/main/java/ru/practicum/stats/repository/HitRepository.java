package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Integer> {
    @Query("select new ru.practicum.stats.dto.StatsDto(h.app, h.uri, count(h.id)) " +
            "from Hit as h " +
            "where h.uri = :uri and h.timestamp between :start and :end " +
            "group by h.app, h.uri order by count(h.id)")
    StatsDto getStatsByUri(@Param("uri") String uri, @Param("start") LocalDateTime start,
                           @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.stats.dto.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit as h " +
            "where h.uri = :uri and h.timestamp between :start and :end " +
            "group by h.app, h.uri order by count(distinct h.ip)")
    StatsDto getStatsByUriUnique(@Param("uri") String uri, @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.stats.dto.StatsDto(h.app, h.uri, count(h.id)) " +
            "from Hit as h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri order by count(h.id)")
    List<StatsDto> getAllStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.stats.dto.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit as h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri order by count(distinct h.ip)")
    List<StatsDto> getAllStatsUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

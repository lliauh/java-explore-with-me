package ru.practicum.explorewithme.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.stats.dto.StatsDto;
import ru.practicum.explorewithme.stats.model.Hit;

import java.time.LocalDateTime;

public interface HitRepository extends JpaRepository<Hit, Integer> {
    @Query("select new ru.practicum.explorewithme.stats.dto.StatsDto(h.app, h.uri, count(h.id)) " +
            "from Hit as h " +
            "where h.uri = :uri and h.timestamp between :start and :end " +
            "group by h.app, h.uri")
    StatsDto getStatsByUri(@Param("uri") String uri, @Param("start") LocalDateTime start,
                           @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.explorewithme.stats.dto.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit as h " +
            "where h.uri = :uri and h.timestamp between :start and :end " +
            "group by h.app, h.uri")
    StatsDto getStatsByUriUnique(@Param("uri") String uri, @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}

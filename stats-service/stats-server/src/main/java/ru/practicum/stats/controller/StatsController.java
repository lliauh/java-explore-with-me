package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.validation.ValidationException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveHit(@RequestBody @Valid HitDto hitDto) {
        log.info("Saving new hit={}", hitDto);

        return statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
                                   @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
                                   @RequestParam(required = false) String[] uris,
                                   @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Getting stats for uris={}; from={}; to={}; unique = {}", uris, startDate, endDate, unique);

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("End time should be greater than start time.");
        }

        return statsService.getStats(startDate, endDate, uris, unique);
    }
}

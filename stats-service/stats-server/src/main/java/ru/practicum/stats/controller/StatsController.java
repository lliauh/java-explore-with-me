package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.validation.StatsRequestValidation;

import javax.validation.Valid;

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
    public StatsDto[] getStats(@RequestParam String start, @RequestParam String end,
                             @RequestParam(required = false) String[] uris,
                             @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Getting stats for uris={}; from={}; to={}; unique = {}", uris, start, end, unique);
        StatsRequestValidation.startEndValidate(start, end);

        return statsService.getStats(start, end, uris, unique);
    }
}

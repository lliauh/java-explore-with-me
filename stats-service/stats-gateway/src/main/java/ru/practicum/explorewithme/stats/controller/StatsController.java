package ru.practicum.explorewithme.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.stats.client.StatsClient;
import ru.practicum.explorewithme.stats.dto.HitDto;
import ru.practicum.explorewithme.stats.validation.StatsRequestValidation;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveHit(@RequestBody @Valid HitDto hitDto) {
        log.info("Saving new hit={}", hitDto);

        return statsClient.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam String start, @RequestParam String end,
                                           @RequestParam String[] uris,
                                           @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Getting stats for uris={}; from={}; to={}; unique = {}", uris, start, end, unique);
        StatsRequestValidation.startEndValidate(start, end);

        return statsClient.getStats(start, end, uris, unique);
    }
}

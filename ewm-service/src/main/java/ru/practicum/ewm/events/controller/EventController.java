package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController {
    private final EventService eventService;

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getUsersEvents(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting events added by user id={}, page starts from={}, size={}", userId, from, size);

        return eventService.getUsersEvents(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEvent) {
        log.info("User id={} is adding new event={}", userId, newEvent);

        return eventService.createEvent(userId, newEvent);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getUsersEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("User id={} is getting event id={}", userId, eventId);

        return eventService.getUsersEventById(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                  @RequestBody @Valid UpdateEventUserRequest updateRequest) {
        log.info("User id={} is updating event id={}, update request={}", userId, eventId, updateRequest);

        return eventService.updateEvent(userId, eventId, updateRequest);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                    @RequestParam(required = false) List<EventState> states,
                                                    @RequestParam(required = false) List<Long> categories,
                                                    @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                        LocalDateTime rangeStart,
                                                    @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                        LocalDateTime rangeEnd,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting events by admin by conditions: users={}, states={}, categories={}, rangeStart={}, " +
                "rangeEnd={}, page starts from={}, size={}", users, states, categories, rangeStart, rangeEnd,
                from, size);

        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequest
            updateRequest) {
        log.info("Event id={} is updating by admin, update request={}", eventId, updateRequest);

        return eventService.updateEventByAdmin(eventId, updateRequest);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "VIEWS") SortType sort,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size,
                                         HttpServletRequest request) {
        log.info("Getting events by conditions: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, " +
                        "onlyAvailable={}, sort={}, page starts from={}, size={}, client ip: {}, endpoint path: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRemoteAddr(),
                request.getRequestURI());

        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Getting event id={}, client ip: {}, endpoint path: {}", eventId, request.getRemoteAddr(),
                request.getRequestURI());

        return eventService.getEventById(eventId, request.getRemoteAddr(), request.getRequestURI());
    }
}

package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getUsersRequests(@PathVariable Long userId) {
        log.info("User id={} is getting requests", userId);

        return requestService.getUsersRequests(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("User id={} is creating request for event id={}", userId, eventId);

        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("User id={} is cancelling request id={}", userId, requestId);

        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUsersRequestsByEvent(@PathVariable Long userId,
                                                                 @PathVariable Long eventId) {
        log.info("User id={} is getting requests on event id={}", userId, eventId);

        return requestService.getUsersRequestsByEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                         @RequestBody @Valid EventRequestStatusUpdateRequest
                                                                 updatedRequestStatus) {
        log.info("User id={} is updating requests status on event id={}, updated requests={}", userId, eventId,
                updatedRequestStatus);

        return requestService.updateRequests(userId, eventId, updatedRequestStatus);
    }
}

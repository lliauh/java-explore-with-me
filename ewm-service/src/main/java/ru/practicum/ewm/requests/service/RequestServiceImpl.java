package ru.practicum.ewm.requests.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.exception.ActionValidationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.requests.dto.*;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.requests.model.RequestStatusUpdate;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.users.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserService userService,
                              UserRepository userRepository, @Lazy EventService eventService,
                              EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        userService.checkIfUserExists(userId);

        return requestRepository.getAllRequestsByUserId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        userService.checkIfUserExists(userId);
        eventService.checkIfEventExists(eventId);

        Request request = new Request();
        request.setRequester(userRepository.getReferenceById(userId));
        request.setEvent(eventRepository.getReferenceById(eventId));
        request.setCreatedOn(LocalDateTime.now());

        Event event = request.getEvent();

        if (event.getInitiator().getId().equals(userId)) {
            throw new ActionValidationException("Initiator cannot send request to his own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ActionValidationException("Request can be send only on published events");
        }

        if (event.getParticipantLimit() > 0
                && event.getParticipantLimit().equals(getConfirmedRequestsByEventId(eventId))) {
            throw new ActionValidationException("Participant limit was reached");
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userService.checkIfUserExists(userId);
        checkIfRequestExists(requestId);

        Request request = requestRepository.getReferenceById(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new ActionValidationException(String.format("User with id=%d doesn't have permissions to cancel " +
                    "request id=%d", userId, requestId));
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUsersRequestsByEvent(Long userId, Long eventId) {
        userService.checkIfUserExists(userId);
        eventService.checkIfEventExists(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        eventService.checkIfUserIsInitiator(userId, event);

        return requestRepository.getAllRequestsByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest updatedRequestStatus) {
        userService.checkIfUserExists(userId);
        eventService.checkIfEventExists(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        eventService.checkIfUserIsInitiator(userId, event);

        List<Request> requestsToStatusChange = requestRepository.getAllRequestsByIds(updatedRequestStatus
                .getRequestIds());

        Set<Long> foundRequestsIds = requestsToStatusChange.stream()
                .map(Request::getId)
                .collect(Collectors.toSet());

        for (Long requestId : updatedRequestStatus.getRequestIds()) {
            if (!foundRequestsIds.contains(requestId)) {
                throw new NotFoundException(String.format("Request with id=%d was not found", requestId));
            }
        }

        Long confirmedRequestsCount = getConfirmedRequestsByEventId(eventId);

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request request : requestsToStatusChange) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ActionValidationException("Only requests with status PENDING can be moderated");
            }

            if (event.getParticipantLimit() != 0 && confirmedRequestsCount.equals(event.getParticipantLimit())) {
                throw new ActionValidationException("Participant limit was reached");
            }

            if (updatedRequestStatus.getStatus().equals(RequestStatusUpdate.CONFIRMED) &&
                    (event.getParticipantLimit() == 0 || confirmedRequestsCount < event.getParticipantLimit())) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequestsCount++;
                confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
            }
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public Long getConfirmedRequestsByEventId(Long eventId) {
        eventService.checkIfEventExists(eventId);

        Optional<List<Request>> requests = requestRepository.getConfirmedRequestsByEventId(eventId);

        return requests.map(requestList -> (long) requestList.size()).orElse(0L);
    }

    @Override
    public Map<Long, Long> getConfirmedRequestsByEventsList(List<Long> eventsIds) {
        List<ConfirmedRequestsDto> confirmedRequestsDtos = requestRepository
                .getConfirmedRequestsByEventsIds(eventsIds);
        return confirmedRequestsDtos.stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEventId,
                        ConfirmedRequestsDto::getConfirmedRequests));
    }

    private void checkIfRequestExists(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new NotFoundException(String.format("Request with id=%d was not found", requestId));
        }
    }
}

package ru.practicum.ewm.requests.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.exception.ActionValidationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.dto.RequestMapper;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.users.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest updatedRequestStatus) {
        userService.checkIfUserExists(userId);
        eventService.checkIfEventExists(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        eventService.checkIfUserIsInitiator(userId, event);

        for (Long requestId : updatedRequestStatus.getRequestIds()) {
            checkIfRequestExists(requestId);
            Request requestToStatusChange = requestRepository.getReferenceById(requestId);

            if (!requestToStatusChange.getStatus().equals(RequestStatus.PENDING)) {
                throw new ActionValidationException("Only requests with status PENDING can be moderated");
            }

            if (!hasEventAvailableSlots(event)) {
                throw new ActionValidationException("Participant limit was reached");
            } else {
                requestToStatusChange.setStatus(RequestStatus.valueOf(updatedRequestStatus.getStatus().toString()));
                requestRepository.save(requestToStatusChange);
                hasEventAvailableSlots(event);
            }
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Long requestId : updatedRequestStatus.getRequestIds()) {
            Request request = requestRepository.getReferenceById(requestId);

            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
            }

            if (request.getStatus().equals(RequestStatus.REJECTED)) {
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
            }
        }

        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);

        return result;
    }

    @Override
    public Integer getConfirmedRequestsByEventId(Long eventId) {
        eventService.checkIfEventExists(eventId);

        Optional<List<Request>> requests = requestRepository.getConfirmedRequestsByEventId(eventId);

        return requests.map(List::size).orElse(0);
    }

    private void checkIfRequestExists(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new NotFoundException(String.format("Request with id=%d was not found", requestId));
        }
    }

    private Boolean hasEventAvailableSlots(Event event) {
        if (event.getParticipantLimit().equals(getConfirmedRequestsByEventId(event.getId()))) {
            List<Request> pendingRequests = requestRepository.getPendingRequestsByEventId(event.getId());
            for (Request request : pendingRequests) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
            }

            return false;
        }

        return true;
    }
}

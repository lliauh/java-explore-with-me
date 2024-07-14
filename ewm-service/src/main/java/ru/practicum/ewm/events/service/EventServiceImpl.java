package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.categories.service.CategoryService;
import ru.practicum.ewm.events.controller.SortType;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.ActionValidationException;
import ru.practicum.ewm.exception.DateValidationException;
import ru.practicum.ewm.exception.EventInitiatorValidationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.requests.service.RequestService;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.users.service.UserService;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final StatsClient statsClient;
    private final RequestService requestService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size) {
        userService.checkIfUserExists(userId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<EventShortDto> result = eventRepository.getAllUserEvents(userId, pageRequest).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        for (EventShortDto event : result) {
            event.setViews(getViewsByEventId(event.getId()));
            event.setConfirmedRequests(requestService.getConfirmedRequestsByEventId(event.getId()));
        }

        return result;
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEvent) {
        userService.checkIfUserExists(userId);
        checkIfEventDateIsValid(newEvent.getEventDate());
        categoryService.checkIfCategoryExists(newEvent.getCategory());

        Event event = EventMapper.toEvent(newEvent);
        event.setInitiator(userRepository.getReferenceById(userId));
        event.setCategory(categoryRepository.getReferenceById(newEvent.getCategory()));
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        Event savedEvent = eventRepository.save(event);

        EventFullDto result = EventMapper.toEventFullDto(savedEvent);
        result.setViews(0L);

        return result;
    }

    @Override
    public EventFullDto getUsersEventById(Long userId, Long eventId) {
        userService.checkIfUserExists(userId);
        checkIfEventExists(eventId);

        Event event = eventRepository.getReferenceById(eventId);

        checkIfUserIsInitiator(userId, event);

        EventFullDto result = EventMapper.toEventFullDto(event);
        result.setViews(getViewsByEventId(eventId));

        return result;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        userService.checkIfUserExists(userId);
        checkIfEventExists(eventId);
        checkIfEventCanBeUpdated(eventId);

        Event event = eventRepository.getReferenceById(eventId);

        checkIfUserIsInitiator(userId, event);

        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getCategory() != null) {
            categoryService.checkIfCategoryExists(updateRequest.getCategory());
            event.setCategory(categoryRepository.getReferenceById(updateRequest.getCategory()));
        }

        if (updateRequest.getEventDate() != null) {
            checkIfEventDateIsValid(updateRequest.getEventDate());
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getLocation() != null) {
            event.setLat(updateRequest.getLocation().getLat());
            event.setLon(updateRequest.getLocation().getLon());
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction().equals(UpdateEventUserStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }

            if (updateRequest.getStateAction().equals(UpdateEventUserStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            }
        }

        EventFullDto result = EventMapper.toEventFullDto(eventRepository.save(event));
        result.setViews(getViewsByEventId(eventId));

        return result;
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                               Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(1000L);
        }

        rangeValidation(rangeStart, rangeEnd);

        List<Event> foundEvents = eventRepository.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd,
                pageRequest);

        List<EventFullDto> result = new ArrayList<>();
        for (Event event : foundEvents) {
            EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
            eventFullDto.setViews(getViewsByEventId(event.getId()));
            eventFullDto.setConfirmedRequests(requestService.getConfirmedRequestsByEventId(event.getId()));
            result.add(eventFullDto);
        }

        return result;
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        checkIfEventExists(eventId);

        Event event = eventRepository.getReferenceById(eventId);

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getCategory() != null) {
            categoryService.checkIfCategoryExists(updateRequest.getCategory());
            event.setCategory(categoryRepository.getReferenceById(updateRequest.getCategory()));
        }

        if (updateRequest.getEventDate() != null) {
            checkIfEventDateIsValid(updateRequest.getEventDate());
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getLocation() != null) {
            event.setLat(updateRequest.getLocation().getLat());
            event.setLon(updateRequest.getLocation().getLon());
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction().equals(UpdateEventAdminStateAction.PUBLISH_EVENT)) {
                if (event.getState().equals(EventState.PENDING)) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new ActionValidationException("Cannot publish the event because it's not in the right " +
                            "state: PUBLISHED");
                }
            }

            if (updateRequest.getStateAction().equals(UpdateEventAdminStateAction.REJECT_EVENT)) {
                if (!event.getState().equals(EventState.PUBLISHED)) {
                    event.setState(EventState.CANCELED);
                } else {
                    throw new ActionValidationException("Cannot reject the event because it's been already published");
                }
            }
        }

        if (event.getState().equals(EventState.PUBLISHED)
                && event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new DateValidationException("You cannot update event less than 1 hour before it starts");
        }

        EventFullDto result = EventMapper.toEventFullDto(eventRepository.save(event));
        result.setViews(getViewsByEventId(eventId));

        return result;
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, SortType sort, Integer from,
                                         Integer size, String clientIp, String uri) {
        saveHit(clientIp, uri);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(1000L);
        }

        rangeValidation(rangeStart, rangeEnd);

        List<Event> foundEvents = eventRepository.getEventsByUser(text, categories, paid, rangeStart, rangeEnd,
                pageRequest);

        List<EventShortDto> result = new ArrayList<>();

        if (onlyAvailable) {
            for (Event event : foundEvents) {
                if (!(event.getParticipantLimit() > 0 &&
                        event.getParticipantLimit().equals(
                                requestService.getConfirmedRequestsByEventId(event.getId())))) {
                    EventShortDto eventShortDto = EventMapper.toEventShortDto(event);

                    eventShortDto.setViews(getViewsByEventId(event.getId()));
                    eventShortDto.setConfirmedRequests(requestService.getConfirmedRequestsByEventId(event.getId()));


                    result.add(eventShortDto);
                }
            }
        } else {
            for (Event event : foundEvents) {
                EventShortDto eventShortDto = EventMapper.toEventShortDto(event);
                eventShortDto.setViews(getViewsByEventId(event.getId()));
                eventShortDto.setConfirmedRequests(requestService.getConfirmedRequestsByEventId(event.getId()));

                result.add(eventShortDto);
            }
        }

        if (sort.equals(SortType.VIEWS)) {
            result.sort(Collections.reverseOrder(Comparator.comparing(EventShortDto::getViews)));
        } else if (sort.equals(SortType.EVENT_DATE)) {
            result.sort(Collections.reverseOrder(Comparator.comparing(EventShortDto::getEventDate)));
        }

        return result;
    }

    @Override
    public EventFullDto getEventById(Long eventId, String clientIp, String uri) {
        saveHit(clientIp, uri);
        checkIfEventExists(eventId);

        Event event = eventRepository.getReferenceById(eventId);
        checkIfEventIsPublished(event);

        EventFullDto result = EventMapper.toEventFullDto(event);

        result.setConfirmedRequests(requestService.getConfirmedRequestsByEventId(eventId));
        result.setViews(getViewsByEventId(eventId));

        return result;
    }

    @Override
    public void checkIfEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
    }

    @Override
    public void checkIfUserIsInitiator(Long userId, Event event) {
        if (!userId.equals(event.getInitiator().getId())) {
            throw new EventInitiatorValidationException("User with id=" + userId + " is not an initiator of event id=" +
                    event.getId());
        }
    }

    private Long getViewsByEventId(Long eventId) {
        String startDate = eventRepository.getReferenceById(eventId).getCreatedOn()
                .format(formatter);
        String endDate = LocalDateTime.now().format(formatter);
        String uri = "/events/" + eventId;
        String[] uris = {uri};

        List<StatsDto> stats = statsClient.getStats(startDate, endDate, uris, true);

        if (stats.isEmpty()) {
            return 0L;
        } else {
            return stats.get(0).getHits();
        }
    }

    private void checkIfEventDateIsValid(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DateValidationException("Field: eventDate. Error: должно содержать дату, которая еще не " +
                    "наступила. Value: " + eventDate);
        }
    }

    private void checkIfEventCanBeUpdated(Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);

        if (event.getState() == EventState.PUBLISHED) {
            throw new ActionValidationException("Only pending or canceled events can be changed");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DateValidationException("It's too late, you can't change event less than 2 hours " +
                    "before start");
        }
    }

    private void checkIfEventIsPublished(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", event.getId()));
        }
    }

    private void saveHit(String clientIp, String uri) {
        HitDto hit = new HitDto();
        hit.setApp("/ewm");
        hit.setUri(uri);
        hit.setIp(clientIp);
        hit.setTimestamp(LocalDateTime.now());

        statsClient.saveHit(hit);
    }

    private void rangeValidation(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeEnd.isBefore(rangeStart)) {
            throw new DateValidationException("Start date cannot be before end date.");
        }
    }
}

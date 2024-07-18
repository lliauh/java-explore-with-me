package ru.practicum.ewm.events.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.categories.dto.CategoryMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.users.dto.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());

        return eventShortDto;
    }

    public static Event toEvent(NewEventDto newEvent) {
        Event event = new Event();

        event.setAnnotation(newEvent.getAnnotation());
        event.setDescription(newEvent.getDescription());
        event.setEventDate(newEvent.getEventDate());
        event.setLat(newEvent.getLocation().getLat());
        event.setLon(newEvent.getLocation().getLon());
        event.setTitle(newEvent.getTitle());

        if (newEvent.getPaid() == null) {
            event.setPaid(false);
        } else {
            event.setPaid(newEvent.getPaid());
        }

        if (newEvent.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        } else {
            event.setParticipantLimit(newEvent.getParticipantLimit());
        }

        if (newEvent.getRequestModeration() == null) {
            event.setRequestModeration(true);
        } else {
            event.setRequestModeration(newEvent.getRequestModeration());
        }

        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());

        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(event.getPublishedOn());
        }

        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());

        return eventFullDto;
    }

    public static EventShortToReviewDto toEventShortToReviewDto(Event event) {
        EventShortToReviewDto eventShortToReviewDto = new EventShortToReviewDto();

        eventShortToReviewDto.setAnnotation(event.getAnnotation());
        eventShortToReviewDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortToReviewDto.setEventDate(event.getEventDate());
        eventShortToReviewDto.setId(event.getId());
        eventShortToReviewDto.setTitle(event.getTitle());

        return eventShortToReviewDto;
    }
}

package ru.practicum.ewm.events.model;

import ru.practicum.ewm.exception.NotFoundException;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static EventState checkEventState(String state) {
        for (EventState enumState : EventState.values()) {
            if (enumState.name().equalsIgnoreCase(state)) {
                return enumState;
            }
        }

        throw new NotFoundException("Unknown event state: UNSUPPORTED_STATUS");
    }
}

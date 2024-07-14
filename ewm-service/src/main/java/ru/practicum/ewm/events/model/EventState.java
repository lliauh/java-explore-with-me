package ru.practicum.ewm.events.model;

import ru.practicum.ewm.exception.NotFoundException;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static void checkEventState(String state) {
        for (EventState enumState : EventState.values()) {
            if (enumState.name().equals(state)) {
                return;
            }
        }

        throw new NotFoundException("Unknown event state: UNSUPPORTED_STATUS");
    }
}

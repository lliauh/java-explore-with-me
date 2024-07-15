package ru.practicum.ewm.requests.dto;

import lombok.Data;
import ru.practicum.ewm.requests.model.RequestStatusUpdate;

import java.util.Set;

@Data
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;

    private RequestStatusUpdate status;
}

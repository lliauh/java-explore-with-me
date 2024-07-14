package ru.practicum.ewm.requests.dto;

import lombok.Data;
import ru.practicum.ewm.requests.model.RequestStatusUpdate;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;

    private RequestStatusUpdate status;
}

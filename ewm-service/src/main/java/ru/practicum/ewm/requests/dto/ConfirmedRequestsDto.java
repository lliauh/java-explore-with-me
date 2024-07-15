package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmedRequestsDto {
    private Long eventId;
    private Long confirmedRequests;
}

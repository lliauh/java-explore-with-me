package ru.practicum.ewm.requests.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.requests.model.Request;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setCreated(request.getCreatedOn());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setId(request.getId());
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setStatus(request.getStatus().toString());

        return participationRequestDto;
    }
}

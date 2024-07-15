package ru.practicum.ewm.events.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.users.dto.UserShortDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventFullDto {
    private String annotation;

    private CategoryDto category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String title;

    private Long views;

    private Long confirmedRequests;
}

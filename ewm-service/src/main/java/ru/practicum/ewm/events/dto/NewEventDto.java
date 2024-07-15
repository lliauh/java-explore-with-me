package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, message = "Annotation length cannot be less than 20 symbols.")
    @Size(max = 2000, message = "Annotation length cannot be greater than 2000 symbols.")
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, message = "Description length cannot be less than 20 symbols.")
    @Size(max = 7000, message = "Description length cannot be greater than 7000 symbols.")
    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    @Valid
    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    @NotBlank
    @Size(min = 3, message = "Title length cannot be less than 3 symbols.")
    @Size(max = 120, message = "Title length cannot be greater than 120 symbols.")
    private String title;
}

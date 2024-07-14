package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, message = "Annotation length cannot be less than 20 symbols.")
    @Size(max = 2000, message = "Annotation length cannot be greater than 2000 symbols.")
    private String annotation;

    private Long category;

    @Size(min = 20, message = "Description length cannot be less than 20 symbols.")
    @Size(max = 7000, message = "Description length cannot be greater than 7000 symbols.")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private UpdateEventAdminStateAction stateAction;

    @Size(min = 3, message = "Title length cannot be less than 3 symbols.")
    @Size(max = 120, message = "Title length cannot be greater than 120 symbols.")
    private String title;
}

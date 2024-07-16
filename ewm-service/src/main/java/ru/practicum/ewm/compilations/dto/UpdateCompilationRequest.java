package ru.practicum.ewm.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;

    private Boolean pinned;

    @Size(min = 1, message = "Title length cannot be less than 1 symbol.")
    @Size(max = 50, message = "Title length cannot be greater than 50 symbols.")
    private String title;
}

package ru.practicum.ewm.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.dto.EventShortDto;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private Set<EventShortDto> events;
}

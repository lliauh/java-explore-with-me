package ru.practicum.explorewithme.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsDto {
    private String app;
    private String uri;
    private Long hits;
}

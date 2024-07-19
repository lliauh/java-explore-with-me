package ru.practicum.ewm.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountLikesByEventDto {
    private Long eventId;
    private Long likesCount;
}

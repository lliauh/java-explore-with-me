package ru.practicum.ewm.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountReviewsByEventDto {
    private Long eventId;
    private Long reviewsCount;
}

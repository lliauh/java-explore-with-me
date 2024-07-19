package ru.practicum.ewm.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.dto.EventShortToReviewDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewToUserDto {
    private Boolean isLike;
    private EventShortToReviewDto event;
}

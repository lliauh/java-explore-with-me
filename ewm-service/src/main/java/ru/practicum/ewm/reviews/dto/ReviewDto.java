package ru.practicum.ewm.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.dto.EventShortToReviewDto;
import ru.practicum.ewm.users.dto.UserShortDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Boolean isLike;
    private EventShortToReviewDto event;
    private UserShortDto reviewer;
}

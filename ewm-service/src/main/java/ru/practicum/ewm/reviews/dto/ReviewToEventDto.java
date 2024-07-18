package ru.practicum.ewm.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.users.dto.UserShortDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewToEventDto {
    private Boolean isLike;
    public UserShortDto reviewer;
}

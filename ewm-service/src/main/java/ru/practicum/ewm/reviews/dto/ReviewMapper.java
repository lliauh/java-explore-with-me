package ru.practicum.ewm.reviews.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.dto.EventMapper;
import ru.practicum.ewm.reviews.model.Review;
import ru.practicum.ewm.users.dto.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {
    public static ReviewToUserDto toReviewToUserDto(Review review) {
        ReviewToUserDto reviewToUserDto = new ReviewToUserDto();
        reviewToUserDto.setIsLike(review.getIsLike());
        reviewToUserDto.setEvent(EventMapper.toEventShortToReviewDto(review.getEvent()));

        return reviewToUserDto;
    }

    public static ReviewToEventDto toReviewToEventDto(Review review) {
        ReviewToEventDto reviewToEventDto = new ReviewToEventDto();
        reviewToEventDto.setIsLike(review.getIsLike());
        reviewToEventDto.setReviewer(UserMapper.toUserShortDto(review.getReviewer()));

        return reviewToEventDto;
    }

    public static ReviewDto toReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setIsLike(review.getIsLike());
        reviewDto.setEvent(EventMapper.toEventShortToReviewDto(review.getEvent()));
        reviewDto.setReviewer(UserMapper.toUserShortDto(review.getReviewer()));

        return reviewDto;
    }
}

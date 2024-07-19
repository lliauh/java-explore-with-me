package ru.practicum.ewm.reviews.service;

import ru.practicum.ewm.reviews.dto.ReviewDto;
import ru.practicum.ewm.reviews.dto.ReviewToEventDto;
import ru.practicum.ewm.reviews.dto.ReviewToUserDto;

import java.util.List;

public interface ReviewService {
    ReviewToUserDto createReview(Long userId, Long eventId, Boolean isLike);

    void deleteReview(Long userId, Long eventId);

    List<ReviewToUserDto> getAllUsersReviews(Long userId, Integer from, Integer size);

    List<ReviewToEventDto> getAllReviewsByEventId(Long eventId, Integer from, Integer size);

    List<ReviewDto> getAllReviewsByEventIdByAdmin(Long eventId, Integer from, Integer size);

    List<ReviewDto> getAllReviewsByAdmin(Integer from, Integer size);
}

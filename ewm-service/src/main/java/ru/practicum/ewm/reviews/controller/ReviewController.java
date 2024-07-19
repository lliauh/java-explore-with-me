package ru.practicum.ewm.reviews.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.reviews.dto.ReviewDto;
import ru.practicum.ewm.reviews.dto.ReviewToEventDto;
import ru.practicum.ewm.reviews.dto.ReviewToUserDto;
import ru.practicum.ewm.reviews.service.ReviewService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/users/{userId}/events/{eventId}/review")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewToUserDto createReview(@PathVariable Long userId, @PathVariable Long eventId,
                                        @RequestParam(defaultValue = "true") Boolean isLike) {
        log.info("User id={} is adding review for event id={}", userId, eventId);

        return reviewService.createReview(userId, eventId, isLike);
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/review")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("User id={} is deleting review for event id={}", userId, eventId);

        reviewService.deleteReview(userId, eventId);
    }

    @GetMapping("/users/{userId}/reviews")
    public List<ReviewToUserDto> getAllUsersReviews(@PathVariable Long userId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("User id={} is getting all his reviews", userId);

        return reviewService.getAllUsersReviews(userId, from, size);
    }

    @GetMapping("/events/{eventId}/reviews")
    public List<ReviewToEventDto> getAllReviewsByEventId(@PathVariable Long eventId,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting all reviews for event id={}", eventId);

        return reviewService.getAllReviewsByEventId(eventId, from, size);
    }

    @GetMapping("/admin/events/{eventId}/reviews")
    public List<ReviewDto> getAllReviewsByEventIdByAdmin(@PathVariable Long eventId,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting all reviews for event id={} by admin", eventId);

        return reviewService.getAllReviewsByEventIdByAdmin(eventId, from, size);
    }

    @GetMapping("/admin/events/reviews")
    public List<ReviewDto> getAllReviewsByAdmin(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting all reviews by admin");

        return reviewService.getAllReviewsByAdmin(from, size);
    }
}

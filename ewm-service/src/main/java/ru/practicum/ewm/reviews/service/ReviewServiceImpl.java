package ru.practicum.ewm.reviews.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.exception.ActionValidationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.reviews.dto.ReviewDto;
import ru.practicum.ewm.reviews.dto.ReviewMapper;
import ru.practicum.ewm.reviews.dto.ReviewToEventDto;
import ru.practicum.ewm.reviews.dto.ReviewToUserDto;
import ru.practicum.ewm.reviews.model.Review;
import ru.practicum.ewm.reviews.repository.ReviewRepository;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.users.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public ReviewToUserDto createReview(Long userId, Long eventId, Boolean isLike) {
        userService.checkIfUserExists(userId);
        eventService.checkIfEventExists(eventId);
        checkIfEventCanBeReviewedByUser(eventId, userId);

        Review review = new Review();
        review.setEvent(eventRepository.getReferenceById(eventId));
        review.setReviewer(userRepository.getReferenceById(userId));
        review.setIsLike(isLike);
        review.setCreatedOn(LocalDateTime.now());

        return ReviewMapper.toReviewToUserDto(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(Long userId, Long eventId) {
        userService.checkIfUserExists(userId);
        eventService.checkIfEventExists(eventId);

        Long reviewId = checkIfReviewExistsAndGetId(userId, eventId);

        reviewRepository.deleteById(reviewId);
    }

    @Override
    public List<ReviewToUserDto> getAllUsersReviews(Long userId, Integer from, Integer size) {
        userService.checkIfUserExists(userId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return reviewRepository.getAllReviewsByUserId(userId, pageRequest).stream()
                .map(ReviewMapper::toReviewToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewToEventDto> getAllReviewsByEventId(Long eventId, Integer from, Integer size) {
        eventService.checkIfEventExists(eventId);
        checkIfEventWasPublishedAndHasPassed(eventId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return reviewRepository.getAllReviewsByEventId(eventId, pageRequest).stream()
                .map(ReviewMapper::toReviewToEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getAllReviewsByEventIdByAdmin(Long eventId, Integer from, Integer size) {
        eventService.checkIfEventExists(eventId);
        checkIfEventWasPublishedAndHasPassed(eventId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return reviewRepository.getAllReviewsByEventId(eventId, pageRequest).stream()
                .map(ReviewMapper::toReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getAllReviewsByAdmin(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return reviewRepository.findAll(pageRequest).stream()
                .map(ReviewMapper::toReviewDto)
                .collect(Collectors.toList());
    }

    private void checkIfEventCanBeReviewedByUser(Long eventId, Long userId) {
        Event event = eventRepository.getReferenceById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED) || event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ActionValidationException(String.format("Event with id=%d cannot be reviewed", eventId));
        }

        Optional<Request> request = requestRepository.getConfirmedRequestByUserIdAndEventId(userId, eventId);

        if (request.isEmpty()) {
            throw new ActionValidationException(String.format("User with id=%d cannot review this event", userId));
        }
    }

    private void checkIfEventWasPublishedAndHasPassed(Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED) || event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ActionValidationException(String.format("Event with id=%d cannot have any reviews, because it " +
                    "wasn't published or hasn't passed yet", eventId));
        }
    }

    public Long checkIfReviewExistsAndGetId(Long userId, Long eventId) {
        Optional<Review> review = reviewRepository.getReviewByUserIdAndEventId(userId, eventId);

        if (review.isEmpty()) {
            throw new NotFoundException(String.format("Review from user id=%d for event id=%d was not found", userId,
                    eventId));
        }

        return review.get().getId();
    }
}

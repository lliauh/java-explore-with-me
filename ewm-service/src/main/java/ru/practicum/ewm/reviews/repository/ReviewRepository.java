package ru.practicum.ewm.reviews.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.reviews.dto.CountLikesByEventDto;
import ru.practicum.ewm.reviews.dto.CountReviewsByEventDto;
import ru.practicum.ewm.reviews.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "select * from reviews where reviewer = :userId and event = :eventId", nativeQuery = true)
    Optional<Review> getReviewByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query(value = "select * from reviews where reviewer = :userId", nativeQuery = true)
    List<Review> getAllReviewsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select * from reviews where event = :eventId", nativeQuery = true)
    List<Review> getAllReviewsByEventId(@Param("eventId") Long eventId, Pageable pageable);

    @Query("select new ru.practicum.ewm.reviews.dto.CountReviewsByEventDto(r.event.id, count(r.id)) " +
            "from Review as r " +
            "where r.event.id in (:eventsIds) " +
            "group by r.event.id")
    List<CountReviewsByEventDto> getReviewsByEvents(@Param("eventsIds") List<Long> eventsIds);


    @Query("select new ru.practicum.ewm.reviews.dto.CountLikesByEventDto(r.event.id, count(r.id)) " +
            "from Review as r " +
            "where r.event.id in (:eventsIds) and isLike = true " +
            "group by r.event.id")
    List<CountLikesByEventDto> getLikesByEvents(@Param("eventsIds") List<Long> eventsIds);
}

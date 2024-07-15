package ru.practicum.ewm.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "select * from events where initiator = :userId order by event_date asc", nativeQuery = true)
    List<Event> getAllUserEvents(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select * from events where category_id = :categoryId order by event_date asc", nativeQuery = true)
    Optional<List<Event>> getAllEventsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("select e from Event e where (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (e.eventDate between :rangeStart and :rangeEnd)")
    List<Event> getEventsByAdmin(@Param("users") List<Long> users, @Param("states") List<EventState> states,
                               @Param("categories") List<Long> categories, @Param("rangeStart") LocalDateTime
                                         rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event e where (:text is null or upper(e.annotation) like upper(concat('%', :text, '%')) or " +
            "upper(e.description) like upper(concat('%', :text, '%'))) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (e.eventDate between :rangeStart and :rangeEnd)")
    List<Event> getEventsByUser(@Param("text") String text, @Param("categories") List<Long> categories,
                                @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event e where e.id in (:eventsIds)")
    List<Event> getEventsByEventsIds(@Param("eventsIds") List<Long> eventIds);


}

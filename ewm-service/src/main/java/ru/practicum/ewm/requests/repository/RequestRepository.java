package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.requests.dto.ConfirmedRequestsDto;
import ru.practicum.ewm.requests.model.Request;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "select * from requests where requester = :userId order by created_on desc", nativeQuery = true)
    List<Request> getAllRequestsByUserId(@Param("userId") Long userId);

    @Query(value = "select * from requests where id in (:requestIds) order by created_on desc", nativeQuery = true)
    List<Request> getAllRequestsByIds(@Param("requestIds") Set<Long> requestIds);

    @Query(value = "select * from requests where event = :eventId and status = 'CONFIRMED' order by created_on desc",
            nativeQuery = true)
    Optional<List<Request>> getConfirmedRequestsByEventId(@Param("eventId") Long eventId);

    @Query(value = "select * from requests where event = :eventId and status = 'PENDING' order by created_on desc",
            nativeQuery = true)
    List<Request> getPendingRequestsByEventId(@Param("eventId") Long eventId);

    @Query(value = "select * from requests where event = :eventId order by created_on desc", nativeQuery = true)
    List<Request> getAllRequestsByEventId(@Param("eventId") Long eventId);

    @Query("select new ru.practicum.ewm.requests.dto.ConfirmedRequestsDto(r.event.id, count(r.id)) " +
            "from Request as r " +
            "where r.event.id in (:eventsIds) " +
            "group by r.event")
    List<ConfirmedRequestsDto> getConfirmedRequestsByEventsIds(@Param("eventsIds") List<Long> eventsIds);
}

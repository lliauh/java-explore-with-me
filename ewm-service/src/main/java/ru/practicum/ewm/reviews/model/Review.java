package ru.practicum.ewm.reviews.model;

import lombok.Data;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer")
    private User reviewer;

    @Column(name = "is_like", nullable = false)
    private Boolean isLike;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
}

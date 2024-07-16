package ru.practicum.ewm.users.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.users.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query( "select u from User u where u.id in :ids" )
    List<User> findUsersByIds(@Param("ids") Long[] ids, Pageable pageable);
}

package ru.practicum.ewm.compilations.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.compilations.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("select c from Compilation c where (:pinned is null or c.pinned = :pinned)")
    public List<Compilation> getAllCompilations(@Param("pinned") Boolean pinned, Pageable pageable);

    /*
    @Query(value = "select * from compilations as c join compilation_events as e on c.id = e.compilation_id " +
            "where pinned = false", nativeQuery = true)
    public List<Compilation> getAllNonpinnedCompilations(Pageable pageable);

     */

}

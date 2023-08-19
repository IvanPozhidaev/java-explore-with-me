package ru.practicum.ewm.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.entity.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("select c from Compilation as c where (:pinned is null or c.pinned = :pinned)")
    List<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);
}
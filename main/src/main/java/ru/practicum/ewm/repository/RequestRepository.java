package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.entity.Request;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findByEventId(Long eventId);
}
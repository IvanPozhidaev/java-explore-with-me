package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findAllByEventId(Long eventId);

    @Query(value = "select count(r.id) from Request r where r.event.id =:eventId and r.status = 'CONFIRMED'")
    Integer countConfirmedByEventId(@Param(value = "eventId") Long eventId);

    @Query(value = "select r from Request r where r.event.id in (:ids) and (r.status = 'CONFIRMED')")
    List<Request> findConfirmedByEventIdIn(List<Long> ids);

    boolean existsByRequesterIdAndEventIdAndStatus(Long userId, Long eventId, RequestStatus status);
}
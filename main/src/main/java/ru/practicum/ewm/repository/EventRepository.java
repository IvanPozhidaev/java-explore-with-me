package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.model.EventState;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Page<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndState(Long eventId, EventState eventState);
}
package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.EventModel;

@EnableJpaRepositories
public interface EventRepository extends JpaRepository<EventModel, Long>, QuerydslPredicateExecutor<EventModel> {

    @Query(value = "select * from events where initiator_id=?1", nativeQuery = true)
    Page<EventModel> findAllByInitiator(Long userId, PageRequest pageRequest);

    @Query(value = "select * from events where id=?1 and initiator_id =?2", nativeQuery = true)
    EventModel findByIdAndAndInitiator(Long eventId, Long userId);

    @Query(value = "select * from events where id=?1 and state=?2", nativeQuery = true)
    EventModel findByIdPublished(Long id, String eventState);
}
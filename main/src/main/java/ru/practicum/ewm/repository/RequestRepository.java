package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.model.RequestModel;

import java.util.List;

@EnableJpaRepositories
public interface RequestRepository extends JpaRepository<RequestModel, Long> {

    @Query(value = "select * from requests where requester_id=?1", nativeQuery = true)
    List<RequestModel> getRequestsInNotHisEvents(Long userId);

    @Query(value = "select * from requests where id=?1 and requester_id =?2", nativeQuery = true)
    RequestModel findByIdAndAndRequester(Long requestId, Long userId);

    @Query(value = "select * from requests where event_id=?1 and requester_id =?2", nativeQuery = true)
    RequestModel checkReRequest(Long eventId, Long userId);

    @Query(value = "select * from requests where event_id=?1", nativeQuery = true)
    List<RequestModel> getEventRequests(Long eventId);
}
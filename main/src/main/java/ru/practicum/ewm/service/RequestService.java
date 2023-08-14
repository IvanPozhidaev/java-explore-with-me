package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.converter.RequestConverter;
import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.entity.model.EventState;
import ru.practicum.ewm.entity.model.RequestStatus;
import ru.practicum.ewm.exception.MainNotFoundException;
import ru.practicum.ewm.exception.MainParamConflictException;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.util.EventUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository,
                          EventRepository eventRepository,
                          UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MainNotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new MainNotFoundException("Event with id=" + eventId + " was not found"));

        Boolean isRequestExist = requestRepository.existsByEventIdAndRequesterId(eventId, userId);
        if (isRequestExist) {
            throw new MainParamConflictException("Unable to add a repeat request");
        }

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new MainParamConflictException("Initiator cannot be requester");
        }
        if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new MainParamConflictException("Unable to participate in an unpublished event");
        }
        if (event.getParticipantLimit() > 0) {
            var countId = EventUtils.countConfirmedRequests(event);
            if (event.getParticipantLimit() <= countId) {
                throw new MainParamConflictException("Request limit with approved status exceeded");
            }
        }

        Request created = RequestConverter.convertToModel(user, event);
        created.setCreated(LocalDateTime.now());

        if (event.getRequestModeration().equals(false) || event.getParticipantLimit() == 0) {
            created.setStatus(RequestStatus.CONFIRMED);
        } else {
            created.setStatus(RequestStatus.PENDING);
        }

        int confirmed = event.getConfirmedRequests();
        int limit = event.getParticipantLimit();

        if (limit == 0) {
            event.setConfirmedRequests(confirmed + 1);
            created.setStatus(RequestStatus.CONFIRMED);
        } else if (confirmed < limit) {
            if (!event.getRequestModeration()) {
                event.setConfirmedRequests(confirmed + 1);
                created.setStatus(RequestStatus.PENDING);
            }
        } else {
            throw new MainParamConflictException(String.format("There are no free places to events with id='%s'",
                    eventId));
        }

        var savedRequest = requestRepository.save(created);
        return RequestConverter.convertToDto(savedRequest);
    }

    public List<RequestDto> getRequestsInNotHisEvents(Long userId) {
        var result = requestRepository.findByRequesterId(userId);
        if (result.size() == 0) {
            return new ArrayList<>();
        }
        return RequestConverter.mapToDto(result);
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        var request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new MainNotFoundException(
                        "Request with id=" + requestId + " from user with id=" + userId + " was not found"));

        request.setStatus(RequestStatus.CANCELED);
        var updatesRequest = requestRepository.save(request);
        return RequestConverter.convertToDto(updatesRequest);
    }
}
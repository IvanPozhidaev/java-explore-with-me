package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.util.EventUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;

    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {
        User user = userService.getIfExistUserById(userId);
        Event event = eventService.getIfExistEventById(eventId);

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

        int confirmedRequests = requestRepository.countConfirmedByEventId(event.getId());
        int limit = event.getParticipantLimit();

        if(limit != 0 && confirmedRequests > limit) {
            throw new MainParamConflictException(String.format("There are no free places to events with id='%s'",
                    eventId));
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            created.setStatus(RequestStatus.CONFIRMED);
        } else {
            created.setStatus(RequestStatus.PENDING);
        }

        var savedRequest = requestRepository.save(created);
        return RequestConverter.convertToDto(savedRequest);
    }

    public List<RequestDto> getRequestsInNotHisEvents(Long userId) {
        var result = requestRepository.findByRequesterId(userId);
        return result.size() == 0 ? Collections.emptyList() : RequestConverter.mapToDto(result);
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        var request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new MainNotFoundException(
                        String.format("Request with id=%s from user with id=%s was not found", requestId, userId)));

        request.setStatus(RequestStatus.CANCELED);
        var updatesRequest = requestRepository.save(request);

        return RequestConverter.convertToDto(updatesRequest);
    }
}
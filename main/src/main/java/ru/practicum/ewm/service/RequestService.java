package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.converter.RequestConverter;
import ru.practicum.ewm.model.dto.RequestDto;
import ru.practicum.ewm.exception.MainNotFoundException;
import ru.practicum.ewm.exception.MainParamConflictException;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public RequestDto addRequest(Long userId, Long eventId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new MainNotFoundException("User with id=" + userId + " was not found");
        }
        var eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new MainNotFoundException("Event with id=" + eventId + " was not found");
        }
        var event = eventOpt.get();
        var checkReRequest = requestRepository.checkReRequest(eventId, userId);
        if (checkReRequest != null) {
            throw new MainParamConflictException("Unable to add a repeat request");
        }
        var checkInitiator = eventRepository.findByIdAndAndInitiator(eventId, userId);
        if (checkInitiator != null) {
            throw new MainParamConflictException("Initiator cannot be requester");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new MainParamConflictException("Unable to participate in an unpublished event");
        }
        if (event.getParticipantLimit() > 0) {
            var countId = event.countConfirmedRequests();
            if (event.getParticipantLimit() <= countId) {
                throw new MainParamConflictException("Request limit with approved status exceeded");
            }
        }
        var created = RequestConverter.convertToModel(userId, eventId);
        created.setCreated(LocalDateTime.now());
        if (event.getRequestModeration().equals(false) || event.getParticipantLimit() == 0) {
            created.setStatus(RequestStatus.CONFIRMED);
        } else {
            created.setStatus(RequestStatus.PENDING);
        }
        var after = requestRepository.save(created);
        return RequestConverter.convertToDto(after);
    }

    public List<RequestDto> getRequestsInNotHisEvents(Long userId) {
        var result = requestRepository.getRequestsInNotHisEvents(userId);
        if (result.size() == 0) {
            return new ArrayList<>();
        }
        return RequestConverter.mapToDto(result);
    }

    public RequestDto cancelRequest(Long userId, Long requestId) {
        var check = requestRepository.findByIdAndAndRequester(requestId, userId);
        if (check == null) {
            throw new MainNotFoundException("Request with id=" + requestId + " from user with id=" + userId + " was not found");
        }
        check.setStatus(RequestStatus.CANCELED);
        var after = requestRepository.save(check);
        return RequestConverter.convertToDto(after);
    }
}
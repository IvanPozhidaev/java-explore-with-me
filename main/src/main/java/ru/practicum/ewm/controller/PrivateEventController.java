package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @Autowired
    public PrivateEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody EventDto eventDto) {
        var addedEvent = eventService.addEvent(userId, eventDto);
        log.info("[POST /users/{userId}/events] (Private). " +
                "Add new event (dto): {}, from user (id): {}", eventDto, userId);
        return addedEvent;
    }

    @GetMapping
    public List<EventShortDto> getEventsByInitiator(@PathVariable Long userId,
                                                    @RequestParam(required = false, defaultValue = "0") int from,
                                                    @RequestParam(required = false, defaultValue = "10") int size) {
        var events = eventService.getAllEventsByInitiatorPrivate(userId, from, size);
        log.info("[GET /users/{userId}/events?from={from}&size={size}] (Private). " +
                "Get events from user (id): {} with param from: {} size: {}", userId, from, size);
        return events;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdPrivate(@PathVariable Long userId, @PathVariable Long eventId) {
        var eventById = eventService.getEventByIdPrivate(userId, eventId);
        log.info("[GET /users/{userId}/events/{eventId}] (Private). Get event (id): {} from user (id): {}", eventId, userId);
        return eventById;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody EventUpdateDto eventDto) {
        var updatedEvent = eventService.updateEventPrivate(userId, eventId, eventDto);
        log.info("[PATCH /users/{userId}/events/{eventsId}] (Private). " +
                "Event {} (id): from user (id): {} update to (dto): {}", userId, eventId, eventDto);
        return updatedEvent;
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getEventRequests(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        var listRequests = eventService.getEventRequests(userId, eventId);
        log.info("[GET /users/{userId}/events/{eventId}/requests] (Private). " +
                "Get requests for event (id): {}, event made by user (id): {}", eventId, userId);
        return listRequests;
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdateResultDto updateStatusRequestsForEvent(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @Valid @RequestBody RequestUpdateDto requestDto) {
        var updatedRequest = eventService.updateStatusRequestsForEvent(userId, eventId, requestDto);
        log.info("[PATCH /users/{userId}/events/{eventId}/requests] (Private). " +
                "Patch requests for event (id): {}, event made by user (id): {}", eventId, userId);
        return updatedRequest;
    }
}
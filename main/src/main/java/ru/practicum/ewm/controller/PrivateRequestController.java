package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.service.RequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable Long userId,
                                 @RequestParam Long eventId) {
        log.info("[POST /users/{userId}/requests?eventId={eventId}] (Private). " +
                "Add new request from user (id): {} to event (id): {}", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getAllRequestsInNotHisEvents(@PathVariable Long userId) {
        log.info("[GET /users/{userId}/requests] (Private). " +
                "Get all request from user (id): {} in not his events", userId);
        return requestService.getRequestsInNotHisEvents(userId);
    }

    @PatchMapping("{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        log.info("[PATCH /users/{userId}/requests/{requestId}/cancel] (Private). " +
                "Cancel request (id): {} from user (id): {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
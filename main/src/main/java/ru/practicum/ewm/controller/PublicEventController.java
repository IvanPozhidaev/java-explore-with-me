package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventSearchDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsPublic(@ModelAttribute EventSearchDto paramSearch,
                                               HttpServletRequest request) {
        log.info("[GET /events?pinned={pinned}&from={from}&size={size}] (Public). " +
                "Get events with params: {}, request={} ", paramSearch, request.getRequestURI());
        return eventService.getEventsPublic(paramSearch, request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByIdPublic(@PathVariable Long id, HttpServletRequest request) {
        log.info("[GET /events/{id}] (Public). Get event (id): {}, from client ip: {}, endpoint path: {}",
                id, request.getRemoteAddr(), request.getRequestURI());
        return eventService.getEventByIdPublic(id, request.getRequestURI(), request.getRemoteAddr());
    }
}
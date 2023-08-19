package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventSearchDto;
import ru.practicum.ewm.dto.EventUpdateDto;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> searchEventsByAdmin(@ModelAttribute EventSearchDto paramSearch) {
        log.info("Search list events (model) with param {}", paramSearch);
        return eventService.searchEventsAdmin(paramSearch.getUsers(), paramSearch.getStates(),
                paramSearch.getCategories(), paramSearch.getRangeStart(), paramSearch.getRangeEnd(),
                paramSearch.getFrom(), paramSearch.getSize());
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @Valid @RequestBody EventUpdateDto eventDto) {
        log.info("[PATCH /admin/events/{eventId}] (Admin). Update event (id): {} to event (dto): {}", eventId, eventDto);
        return eventService.updateEventByAdmin(eventId, eventDto);
    }
}
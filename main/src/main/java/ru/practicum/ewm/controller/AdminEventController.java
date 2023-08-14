package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventDtoFull;
import ru.practicum.ewm.dto.EventUpdateDto;
import ru.practicum.ewm.entity.model.EventState;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @Autowired
    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDtoFull> searchEventsByAdmin(@RequestParam(required = false) Long[] users,
                                                  @RequestParam(required = false) EventState states,
                                                  @RequestParam(required = false) Long[] categories,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size) {
        var foundedEvents = eventService.searchEventsAdmin(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );
        log.info("[GET /admin/events?users={users}&states={states}&categories={categories}&rangeStart={rangeStart}&rangeEnd={rangeEnd}&from={from}&size={size}] (Admin). " +
                        "Search list events (model) with param users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return foundedEvents;
    }

    @PatchMapping("/{eventId}")
    public EventDtoFull updateEventByAdmin(@PathVariable Long eventId,
                                           @Valid @RequestBody EventUpdateDto eventDto) {
        log.info("eventId: {}, eventDto: {}, eventService: {}", eventId, eventDto, eventService);
        var updatedEventByAdmin = eventService.updateEventByAdmin(eventId, eventDto);
        log.info("[PATCH /admin/events/{eventId}] (Admin). Update event (id): {} to event (dto): {}", eventId, eventDto);
        return updatedEventByAdmin;
    }
}
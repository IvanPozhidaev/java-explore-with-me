package ru.practicum.ewm.stats.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.exception.ParameterException;
import ru.practicum.ewm.stats.collective.HitDto;
import ru.practicum.ewm.stats.collective.StatsDto;
import ru.practicum.ewm.stats.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
public class StatsController {

    private final StatsService statsService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void addHit(@RequestBody HitDto hitDto) {
        statsService.addHit(hitDto);
        log.info("[POST /hit]. Save request info (app: {}, client ip: {}, endpoint path: {}, datetime: {})",
                hitDto.getApp(), hitDto.getIp(), hitDto.getUri(), hitDto.getTimestamp());
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam(required = false) String[] uris,
                                   @RequestParam(required = false, defaultValue = "false") String unique) {
        var result = statsService.getStats(parseTime(start), parseTime(end), uris, parseBool(unique));
        log.info("[GET /stats?start={start}&end={end}&uris={uris}&unique={unique}]. Get stats from date: {} to date: {} for uris: {} (unique: {})",
                start, end, uris, unique);
        return result;
    }

    private LocalDateTime parseTime(String dateTime) {
        return LocalDateTime.parse(dateTime, formatter);
    }

    private boolean parseBool(String unique) {
        boolean bool;
        try {
            bool = Boolean.parseBoolean(unique);
        } catch (IllegalArgumentException e) {
            throw new ParameterException("Unknown param unique: " + unique);
        }
        return bool;
    }
}
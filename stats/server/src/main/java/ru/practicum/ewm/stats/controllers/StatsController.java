package ru.practicum.ewm.stats.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.collective.HitDto;
import ru.practicum.ewm.stats.collective.StatsDto;
import ru.practicum.ewm.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
public class StatsController {

    private final StatsService statsService;

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
    public List<StatsDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                     @RequestParam(required = false) List<String> uris,
                                     @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        var result = statsService.getStats(start, end, uris, unique);
        log.info("[GET /stats?start={start}&end={end}&uris={uris}&unique={unique}]. Get stats from date: {} to date: {} for uris: {} (unique: {})",
                start, end, uris, unique);
        return result;
    }
}
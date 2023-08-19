package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("[GET /compilations?pinned={pinned}&from={from}&size={size}] (Public). " +
                "Get compilations with param pinned: {}, from: {}, size: {}.", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationsById(@PathVariable Long compId) {
        log.info("[GET /compilations?pinned={pinned}&from={from}&size={size}] (Public). " +
                "Get compilation (id): {}", compId);
        return compilationService.getCompilationsById(compId);
    }
}
package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.CompilationNewDto;
import ru.practicum.ewm.dto.CompilationUpdateDto;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody CompilationNewDto compilationDto) {
        log.info("[POST /admin/compilations] (Admin). Added new compilation (dto) {}", compilationDto);
        return compilationService.addCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody CompilationUpdateDto compilationDto) {
        log.info("[PATCH /admin/compilations{compId}] (Admin). Compilation (id) {} update to (dto): {}", compId, compilationDto);
        return compilationService.updateCompilation(compId, compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("[DELETE /admin/compilations{compId}] (Admin). Delete compilation (id): {}", compId);
        compilationService.deleteCompilation(compId);
    }
}
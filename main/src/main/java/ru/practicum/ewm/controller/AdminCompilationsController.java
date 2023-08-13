package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.CompilationDto;
import ru.practicum.ewm.model.dto.CompilationUpdateDto;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {

    private final CompilationService compilationService;

    @Autowired
    public AdminCompilationsController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody CompilationDto compilationDto) {
        var addedComp = compilationService.addCompilation(compilationDto);
        log.info("[POST /admin/compilations] (Admin). Added new compilation (dto) {}", compilationDto);
        return addedComp;
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody CompilationUpdateDto compilationDto) {
        var updatedComp = compilationService.updateCompilation(compId, compilationDto);
        log.info("[PATCH /admin/compilations{compId}] (Admin). Compilation (id) {} update to (dto): {}", compId, compilationDto);
        return updatedComp;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        log.info("[DELETE /admin/compilations{compId}] (Admin). Delete compilation (id): {}", compId);
    }
}
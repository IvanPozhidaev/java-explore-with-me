package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.converter.CompilationConverter;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.CompilationNewDto;
import ru.practicum.ewm.dto.CompilationUpdateDto;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.exception.MainNotFoundException;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.util.PageHelper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationDto addCompilation(CompilationNewDto compilationDto) {
        var create = CompilationConverter.convertToModel(compilationDto);

        if (!Objects.nonNull(compilationDto.getPinned())) {
            create.setPinned(false);
        }

        if (Objects.nonNull(compilationDto.getEvents())) {
            List<Event> getEvent = eventRepository.findAllById(compilationDto.getEvents());
            create.setEvents(getEvent);
        }
        var after = compilationRepository.save(create);
        return CompilationConverter.convertToDto(after);
    }

    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto compilationDto) {
        var updatedComp = compilationRepository.findById(compId)
                .orElseThrow(() -> new MainNotFoundException(String.format(
                        "Compilation with id=%s was not found", compId)));

        if (compilationDto.getTitle() != null) {
            updatedComp.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            updatedComp.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getEvents() != null) {
            var events = eventRepository.findAllById(compilationDto.getEvents());
            updatedComp.setEvents(events);
        }

        var after = compilationRepository.save(updatedComp);
        return CompilationConverter.convertToDto(after);
    }

    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageHelper.createRequest(from, size);
        List<Compilation> result;
        result = (pinned != null)
                ? compilationRepository.findAllByPinned(pinned, pageRequest)
                : compilationRepository.findAll(pageRequest).getContent();

        return result.size() == 0 ? Collections.emptyList() : CompilationConverter.mapToDto(result);
    }

    public CompilationDto getCompilationsById(Long compId) {
        var result = compilationRepository.findById(compId).orElseThrow(() ->
                new MainNotFoundException(String.format("Compilation with id=%s was not found", compId)));

        return CompilationConverter.convertToDto(result);
    }
}
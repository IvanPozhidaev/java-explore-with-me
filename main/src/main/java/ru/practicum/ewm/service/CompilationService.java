package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.converter.CompilationConverter;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.CompilationUpdateDto;
import ru.practicum.ewm.exception.MainNotFoundException;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.util.PageHelper;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompilationService {

    private final CompilationRepository compilationRepository;

    @Autowired
    public CompilationService(CompilationRepository compilationRepository) {
        this.compilationRepository = compilationRepository;
    }

    public CompilationDto addCompilation(CompilationDto compilationDto) {
        var create = CompilationConverter.convertToModel(compilationDto);
        var after = compilationRepository.save(create);
        return CompilationConverter.convertToDto(after);
    }

    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto compilationDto) {
        var updatedComp = compilationRepository.findById(compId)
                .orElseThrow(() ->  new MainNotFoundException("Compilation with id=" + compId + " was not found"));

        if (compilationDto.getTitle() != null) {
            updatedComp.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            updatedComp.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getEvents() != null) {
            updatedComp.setEvents(compilationDto.getEvents());
        }
        var after = compilationRepository.save(updatedComp);
        return CompilationConverter.convertToDto(after);
    }

    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageHelper.createRequest(from, size);
        if (pinned != null) {
            if (pinned) {
                var compilationPinned = compilationRepository.findAllByPinned(true, pageRequest).getContent();
                if (compilationPinned.size() == 0) {
                    return new ArrayList<>();
                }
                return CompilationConverter.mapToDto(compilationPinned);
            } else {
                var compilationNotPinned = compilationRepository.findAllByPinned(false, pageRequest).getContent();
                if (compilationNotPinned.size() == 0) {
                    return new ArrayList<>();
                }
                return CompilationConverter.mapToDto(compilationNotPinned);
            }
        } else {
            var allComp = compilationRepository.findAll(pageRequest).getContent();
            return CompilationConverter.mapToDto(allComp);
        }
    }

    public CompilationDto getCompilationsById(Long compId) {
        var result = compilationRepository.findById(compId)
                .orElseThrow(() -> new MainNotFoundException("Compilation with id=" + compId + " was not found"));
        return CompilationConverter.convertToDto(result);
    }
}
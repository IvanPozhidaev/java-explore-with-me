package ru.practicum.ewm.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.CompilationNewDto;
import ru.practicum.ewm.entity.Compilation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationConverter {

    public Compilation convertToModel(CompilationDto dto) {
        Compilation model = new Compilation();
        model.setTitle(dto.getTitle());
        model.setPinned(dto.getPinned());
        return model;
    }

    public Compilation convertToModel(CompilationNewDto dto) {
        Compilation model = new Compilation();
        model.setTitle(dto.getTitle());
        model.setPinned(dto.getPinned());
        return model;
    }

    public CompilationDto convertToDto(Compilation model) {
        return new CompilationDto(
                model.getId(),
                model.getTitle(),
                model.getPinned(),
                model.getEvents() == null ? Collections.emptyList() : EventConverter.mapToShortDto(model.getEvents())
        );
    }

    public List<CompilationDto> mapToDto(List<Compilation> comps) {
        return comps.stream().map(CompilationConverter::convertToDto).collect(Collectors.toList());
    }

}
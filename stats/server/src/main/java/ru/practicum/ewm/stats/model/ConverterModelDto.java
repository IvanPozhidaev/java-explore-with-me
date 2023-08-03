package ru.practicum.ewm.stats.model;

import ru.practicum.ewm.stats.collective.HitDto;
import ru.practicum.ewm.stats.collective.StatsDto;

import java.util.ArrayList;
import java.util.List;

public class ConverterModelDto {

    public static HitModel convertToModel(HitDto dto) {
        HitModel model = new HitModel();
        model.setApp(dto.getApp());
        model.setUri(dto.getUri());
        model.setIp(dto.getIp());
        model.setTimestamp(dto.getTimestamp());
        return model;
    }

    public static StatsDto convertToDto(StatsModel model) {
        StatsDto dto = new StatsDto();
        dto.setApp(model.getApp());
        dto.setUri(model.getUri());
        dto.setHits(model.getHits());
        return dto;
    }

    public static List<StatsDto> mapToDto(List<StatsModel> listModel) {
        List<StatsDto> listDto = new ArrayList<>();
        for (StatsModel m : listModel) {
            listDto.add(convertToDto(m));
        }
        return listDto;
    }
}
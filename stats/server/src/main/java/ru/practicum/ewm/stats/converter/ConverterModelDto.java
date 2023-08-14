package ru.practicum.ewm.stats.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.collective.HitDto;
import ru.practicum.ewm.stats.entity.Hit;

@UtilityClass
public class ConverterModelDto {

    public Hit convertToModel(HitDto dto) {
        Hit model = new Hit();
        model.setApp(dto.getApp());
        model.setUri(dto.getUri());
        model.setIp(dto.getIp());
        model.setDateTime(dto.getTimestamp());
        return model;
    }

}
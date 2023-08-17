package ru.practicum.ewm.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.EventDto;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.util.EventUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventConverter {

    public Event convertToModel(User user, EventDto dto) {
        Event model = new Event();
        model.setTitle(dto.getTitle());
        model.setDescription(dto.getDescription());
        model.setAnnotation(dto.getAnnotation());
        model.setInitiator(user);
        model.setEventDate(dto.getEventDate());
        model.setParticipantLimit(dto.getParticipantLimit());
        model.setRequestModeration(dto.getRequestModeration());
        model.setPaid(dto.getPaid());
        return model;
    }

    public EventShortDto convertToShortDto(Event model) {
        EventShortDto dto = new EventShortDto();
        dto.setId(model.getId());
        dto.setTitle(model.getTitle());
        dto.setDescription(model.getDescription());
        dto.setAnnotation(model.getAnnotation());
        dto.setCategory(CategoryConverter.convertToDto(model.getCategory()));
        dto.setInitiator(UserConverter.convertToDto(model.getInitiator()));
        dto.setConfirmedRequests(EventUtils.countConfirmedRequests(model));
        dto.setEventDate(model.getEventDate());
        dto.setPaid(model.getPaid());
        return dto;
    }

    public List<EventShortDto> mapToShortDto(List<Event> events) {
        List<EventShortDto> res = new ArrayList<>();
        for (Event e : events) {
            res.add(convertToShortDto(e));
        }
        return res;
    }

    public EventFullDto convertToDtoFull(Event model) {
        EventFullDto dtoFull = new EventFullDto();
        dtoFull.setId(model.getId());
        dtoFull.setTitle(model.getTitle());
        dtoFull.setDescription(model.getDescription());
        dtoFull.setAnnotation(model.getAnnotation());
        dtoFull.setLocation(new LocationDto(model.getLocation().getLat(), model.getLocation().getLon()));
        dtoFull.setEventDate(model.getEventDate());
        dtoFull.setParticipantLimit(model.getParticipantLimit());
        dtoFull.setRequestModeration(model.getRequestModeration());
        dtoFull.setPaid(model.getPaid());
        dtoFull.setCategory(CategoryConverter.convertToDto(model.getCategory()));
        dtoFull.setConfirmedRequests(EventUtils.countConfirmedRequests(model));
        dtoFull.setCreatedOn(model.getCreatedOn());
        dtoFull.setInitiator(UserConverter.convertToDto(model.getInitiator()));
        dtoFull.setPublishedOn(model.getPublishedOn());
        dtoFull.setState(model.getState());
        return dtoFull;
    }

    public List<EventFullDto> mapToDtoFull(List<Event> events) {
        return events.stream().map(EventConverter::convertToDtoFull).collect(Collectors.toList());
    }
}
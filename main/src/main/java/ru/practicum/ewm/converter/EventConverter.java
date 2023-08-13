package ru.practicum.ewm.converter;

import ru.practicum.ewm.model.dto.EventDto;
import ru.practicum.ewm.model.dto.EventShortDto;
import ru.practicum.ewm.model.dto.EventDtoFull;
import ru.practicum.ewm.model.EventModel;
import ru.practicum.ewm.model.UserModel;
import ru.practicum.ewm.model.dto.LocationDto;

import java.util.ArrayList;
import java.util.List;

public class EventConverter {

    public static EventModel convertToModel(UserModel user, EventDto dto) {
        EventModel model = new EventModel();
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

    public static EventShortDto convertToShortDto(EventModel model) {
        EventShortDto dto = new EventShortDto();
        dto.setId(model.getId());
        dto.setTitle(model.getTitle());
        dto.setDescription(model.getDescription());
        dto.setAnnotation(model.getAnnotation());
        dto.setCategory(CategoryConverter.convertToDto(model.getCategory()));
        dto.setInitiator(UserConverter.convertToDto(model.getInitiator()));
        dto.setConfirmedRequests(model.countConfirmedRequests());
        dto.setEventDate(model.getEventDate());
        dto.setPaid(model.getPaid());
        return dto;
    }

    public static List<EventShortDto> mapToShortDto(List<EventModel> events) {
        List<EventShortDto> res = new ArrayList<>();
        for (EventModel e : events) {
            res.add(convertToShortDto(e));
        }
        return res;
    }

    public static EventDtoFull convertToDtoFull(EventModel model) {
        EventDtoFull dtoFull = new EventDtoFull();
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
        dtoFull.setConfirmedRequests(model.countConfirmedRequests());
        dtoFull.setCreatedOn(model.getCreatedOn());
        dtoFull.setInitiator(UserConverter.convertToDto(model.getInitiator()));
        dtoFull.setPublishedOn(model.getPublishedOn());
        dtoFull.setState(model.getState());
        return dtoFull;
    }

    public static List<EventDtoFull> mapToDtoFull(List<EventModel> events) {
        List<EventDtoFull> resFull = new ArrayList<>();
        for (EventModel e : events) {
            resFull.add(convertToDtoFull(e));
        }
        return resFull;
    }
}
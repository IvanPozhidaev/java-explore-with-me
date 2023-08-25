package ru.practicum.ewm.converter;

import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class RequestConverter {

    public static Request convertToModel(Long userId, Long eventId) {
        Request model = new Request();
        model.setEvent(Event.builder().id(eventId).build());
        model.setRequester(User.builder().id(userId).build());
        return model;
    }

    public static Request convertToModel(User user, Event event) {
        Request model = new Request();
        model.setEvent(event);
        model.setRequester(user);
        return model;
    }

    public static RequestDto convertToDto(Request model) {
        RequestDto dto = new RequestDto();
        dto.setId(model.getId());
        dto.setEvent(model.getEvent().getId());
        dto.setRequester(model.getRequester().getId());
        dto.setCreated(model.getCreated());
        dto.setStatus(model.getStatus());
        return dto;
    }

    public static List<RequestDto> mapToDto(List<Request> requests) {
        return requests.stream().map(RequestConverter::convertToDto).collect(Collectors.toList());

    }
}
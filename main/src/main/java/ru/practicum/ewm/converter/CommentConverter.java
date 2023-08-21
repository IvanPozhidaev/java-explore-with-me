package ru.practicum.ewm.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentShortDto;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentConverter {
    public static CommentDto convertToDto(Comment model) {
        return new CommentDto(
                model.getId(),
                model.getText(),
                model.getAuthor().getName(),
                model.getCreated()
        );
    }

    public static Comment convertToModel(User user, Event event, CommentDto dto) {
        Comment model = new Comment();
        model.setText(dto.getText());
        model.setAuthor(user);
        model.setEvent(event);
        model.setCreated(dto.getCreated());
        return model;
    }

    public static List<CommentDto> mapToDto(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        List<CommentDto> result = new ArrayList<>();
        for (Comment c : comments) {
            result.add(convertToDto(c));
        }
        return result;
    }

    public static CommentShortDto convertToShortDto(Comment model) {
        return new CommentShortDto(
                model.getText(),
                model.getAuthor().getName(),
                model.getCreated()
        );
    }

    public static List<CommentShortDto> mapToShortDto(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        List<CommentShortDto> result = new ArrayList<>();
        for (Comment c : comments) {
            result.add(convertToShortDto(c));
        }
        return result;
    }
}
package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.converter.CommentConverter;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentShortDto;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.model.EventState;
import ru.practicum.ewm.entity.model.RequestStatus;
import ru.practicum.ewm.exception.MainNotFoundException;
import ru.practicum.ewm.exception.MainParameterException;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.util.PageHelper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public CommentShortDto addComment(Long userId, Long eventId, CommentDto commentDto) {
        var user = userService.getIfExistUserById(userId);
        var event = getIfExistEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new MainParameterException("Only published event can be commented");
        }
        if (commentDto.getCreated().isAfter(event.getEventDate()) && (event.getRequestModeration() || event.getParticipantLimit() != 0)) {
            boolean rq = requestRepository.existsByRequesterIdAndEventIdAndStatus(userId, eventId, RequestStatus.CONFIRMED);
            if (!rq || !userId.equals(event.getInitiator().getId())) {
                throw new MainParameterException("Only confirmed requester or initiator can leave comments when event get started");
            }
        }
        var after = commentRepository.save(CommentConverter.convertToModel(user, event, commentDto));
        return CommentConverter.convertToShortDto(after);
    }

    public CommentShortDto updateComment(Long userId, Long eventId, Long commentId, CommentDto updCommentDto) {
        userService.checkExistUserById(userId);
        checkExistEventById(eventId);

        var comment = getIfExistCommentById(commentId);

        if (!userId.equals(comment.getAuthor().getId())) {
            throw new MainParameterException("Only author can update comment");
        }

        if (comment.getText().equals(updCommentDto.getText())) {
            throw new MainParameterException("Comment text not changed");
        }

        comment.setText(updCommentDto.getText());
        var after = commentRepository.save((comment));

        return CommentConverter.convertToShortDto(after);
    }

    public void deleteComment(Long userId, Long eventId, Long commentId) {
        userService.checkExistUserById(userId);
        var event = getIfExistEventById(eventId);
        var comment = getIfExistCommentById(commentId);

        if (userId.equals(event.getInitiator().getId()) || userId.equals(comment.getAuthor().getId())) {
            commentRepository.deleteById(commentId);
        } else {
            throw new MainParameterException("Only author or event initiator can delete comment");
        }
    }

    public void deleteCommentByAdmin(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public CommentShortDto getCommentByIdForEvent(Long userId, Long eventId, Long commentId) {
        userService.checkExistUserById(userId);
        checkExistEventById(eventId);

        var commentById = commentRepository.findByIdForEvent(EventState.PUBLISHED.toString(), eventId, commentId)
                .orElseThrow(() -> new MainNotFoundException(String.format("Comment with id=%s was not found", commentId)));

        return CommentConverter.convertToShortDto(commentById);
    }

    public List<CommentShortDto> getPublishedCommentsForEvent(Long eventId, int size, int from) {
        checkExistEventById(eventId);

        PageRequest pageRequest = PageHelper.createRequest(from, size);
        var pageAllComments = commentRepository.findAllByStateAndEventId(EventState.PUBLISHED.toString(), eventId, pageRequest);
        return CommentConverter.mapToShortDto(pageAllComments);
    }

    public List<CommentShortDto> getPublishedCommentsForEvent(Long userId, Long eventId, int size, int from) {
        userService.checkExistUserById(userId);
        checkExistEventById(eventId);

        PageRequest pageRequest = PageHelper.createRequest(from, size);
        var pageAllCommentsForEvent = commentRepository.findAllByStateAndEventId(EventState.PUBLISHED.toString(), eventId, pageRequest);
        return CommentConverter.mapToShortDto(pageAllCommentsForEvent);
    }

    private Comment getIfExistCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new MainNotFoundException(String.format("Comment with id=%s was not found", commentId)));
    }

    private Event getIfExistEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new MainNotFoundException(String.format("Event with id=%s was not found", eventId)));
    }

    private void checkExistEventById(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new MainNotFoundException(String.format("Event with id=%s was not found", eventId));
        }
    }
}
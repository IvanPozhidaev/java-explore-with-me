package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.converter.CommentConverter;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentShortDto;
import ru.practicum.ewm.entity.model.EventState;
import ru.practicum.ewm.entity.model.RequestStatus;
import ru.practicum.ewm.exception.MainNotFoundException;
import ru.practicum.ewm.exception.MainParameterException;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.util.PageHelper;

import java.util.List;

@Service
public class CommentService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(EventRepository eventRepository,
                          UserRepository userRepository,
                          RequestRepository requestRepository,
                          CommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public CommentShortDto addComment(Long userId, Long eventId, CommentDto commentDto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new MainNotFoundException("User with id=" + userId + " was not found"));
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new MainNotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new MainParameterException("Only published event can be commented");
        }
        if (commentDto.getCreated().isAfter(event.getEventDate()) && (event.getRequestModeration() || event.getParticipantLimit() != 0)) {
            boolean rq = requestRepository.existsByRequesterAndEventAndStatus(userId, eventId, RequestStatus.CONFIRMED);
            if (!rq || !userId.equals(event.getInitiator().getId())) {
                throw new MainParameterException("Only confirmed requester or initiator can leave comments when event get started");
            }
        }
        var after = commentRepository.save(CommentConverter.convertToModel(user, event, commentDto));
        return CommentConverter.convertToShortDto(after);
    }

    public CommentShortDto updateComment(Long userId, Long eventId, Long commentId, CommentDto updCommentDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new MainNotFoundException("User with id=" + userId + " was not found"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new MainNotFoundException("Event with id=" + eventId + " was not found"));
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new MainNotFoundException("Comment with id=" + commentId + " was not found"));
        if (userId.equals(comment.getAuthor().getId())) {
            if (comment.getText().equals(updCommentDto.getText())) {
                throw new MainParameterException("Comment text not changed");
            }
            comment.setText(updCommentDto.getText());
            var after = commentRepository.save((comment));
            return CommentConverter.convertToShortDto(after);
        } else {
            throw new MainParameterException("Only author can update comment");
        }
    }

    public void deleteComment(Long userId, Long eventId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new MainNotFoundException("User with id=" + userId + " was not found"));
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new MainNotFoundException("Event with id=" + eventId + " was not found"));
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new MainNotFoundException("Comment with id=" + commentId + " was not found"));
        if (userId.equals(event.getInitiator().getId()) || userId.equals(comment.getAuthor().getId())) {
            commentRepository.deleteById(commentId);
        } else {
            throw new MainParameterException("Only author or event initiator can delete comment");
        }
    }

    public CommentShortDto getCommentByIdForEvent(Long userId, Long eventId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new MainNotFoundException("User with id=" + userId + " was not found"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new MainNotFoundException("Event with id=" + eventId + " was not found"));
        var commentById = commentRepository.getByIdForEvent(EventState.PUBLISHED.toString(), eventId, commentId);
        if (commentById == null) {
            throw new MainNotFoundException("Comment with id=" + commentId + " was not found");
        }
        return CommentConverter.convertToShortDto(commentById);
    }

    public List<CommentShortDto> getPublishedCommentsForEvent(Long userId, Long eventId, int size, int from) {
        userRepository.findById(userId)
                .orElseThrow(() -> new MainNotFoundException("User with id=" + userId + " was not found"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new MainNotFoundException("Event with id=" + eventId + " was not found"));
        PageRequest pageRequest = PageHelper.createRequest(from, size);
        var pageAllCommentsForEvent = commentRepository.getAllForEvent(EventState.PUBLISHED.toString(), eventId, pageRequest);
        return CommentConverter.mapToShortDto(pageAllCommentsForEvent.getContent());
    }
}
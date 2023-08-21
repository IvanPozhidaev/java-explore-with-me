package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}/comment")
    public CommentShortDto addComment(@PathVariable Long userId,
                                      @PathVariable Long eventId,
                                      @Valid @RequestBody CommentDto commentDto) {
        var addedCommentDto = commentService.addComment(userId, eventId, commentDto);
        log.info("[POST /users/{userId}/events/{eventId}/comment] (Private). " +
                "Add comment (dto): {}, from user (id): {}, for event (id): {}", commentDto, eventId, userId);
        return addedCommentDto;
    }

    @PatchMapping("/{eventId}/comment/{commentId}")
    public CommentShortDto updateComment(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @PathVariable Long commentId,
                                         @Valid @RequestBody CommentDto updCommentDto) {
        var res = commentService.updateComment(userId, eventId, commentId, updCommentDto);
        log.info("[PATCH /users/{userId}/events/{eventId}/comment/{commentId}] (Private). " +
                "Patch comment (id): {} for event (id): {}, to comment (dto): {}, by user (id): {}", userId, eventId, commentId, updCommentDto);
        return res;
    }

    @DeleteMapping("/{eventId}/comment/{commentId}")
    public void deletedComment(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @PathVariable Long commentId) {
        commentService.deleteComment(userId, eventId, commentId);
        log.info("[DELETE /users/{userId}/events/{eventId}/comment/{commentId}] (Private). " +
                "Delete comment (id): {} for event (id): {}, by user (id): {}", userId, eventId, commentId);
    }

    @GetMapping("/{eventId}/comment/{commentId}")
    public CommentShortDto getCommentByIdForEvent(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @PathVariable Long commentId) {
        var commentById = commentService.getCommentByIdForEvent(userId, eventId, commentId);
        log.info("[GET /users/{userId}/events/{eventId}/comment/{commentId}] (Private). " +
                "Get comment (id): {} for event (id): {}, by user (id): {}", userId, eventId, commentId);
        return commentById;
    }

    @GetMapping("/{eventId}/comment")
    public List<CommentShortDto> getPublishedCommentsForEvent(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestParam(required = false, defaultValue = "0") int from,
                                                              @RequestParam(required = false, defaultValue = "10") int size) {
        var listComments = commentService.getPublishedCommentsForEvent(userId, eventId, size, from);
        log.info("[GET /users/{userId}/events/{eventId}/comment/] (Private). " +
                "Get published comments (dto) for event (id): {}, from: {} to: {}, request by user (id): {}", eventId, from, size, userId);
        return listComments;
    }
}
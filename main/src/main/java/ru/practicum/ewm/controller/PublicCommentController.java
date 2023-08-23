package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentShortDto;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;


    @GetMapping("/comments")
    public List<CommentShortDto> getPublishedCommentsForEvent(@PathVariable Long eventId,
                                                              @RequestParam(required = false, defaultValue = "0") int from,
                                                              @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("[GET /events/{eventId}/comments] (Public). " +
                "Get published comments (dto) for event (id): {}, from: {} to: {}", eventId, from, size);
        return commentService.getPublishedCommentsForEvent(eventId, size, from);
    }
}
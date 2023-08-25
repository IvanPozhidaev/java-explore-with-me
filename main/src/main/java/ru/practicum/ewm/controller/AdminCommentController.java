package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/admin/events/comments")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    public void deletedComment(@PathVariable Long commentId) {
        log.info("[DELETE /admin/events/comments] (Admin). " +
                "Delete comment by admin (id): {} ", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}
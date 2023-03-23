package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.service.CommentService;
import com.sharememories.sharememories.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    private CommentService service;

    @Autowired
    public CommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Comment> comment = service.getComment(id);
        if (comment.isPresent()) {
            return ResponseEntity.ok(comment.get());
        } else {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Comment not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @DeleteMapping("/{commentId}/post/{postId}")
    public ResponseEntity<?> deleteComment(@PathVariable long postId,
                                           @PathVariable long commentId) {
        Optional<String> commentImageName = service.getCommentImageName(commentId);
        if (commentImageName.isPresent()) {
            try {
                FileUtils.deleteFile(Comment.IMAGES_DIRECTORY_PATH, commentImageName.get());
            } catch (IOException e) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                map.put("message", "Error while deleting Comment image.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
            }
        }
        service.deletePostComment(postId, commentId);
        return ResponseEntity.ok().build();
    }
}

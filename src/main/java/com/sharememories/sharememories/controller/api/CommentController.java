package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.CommentService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    private CommentService commentService;
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    public CommentController(CommentService commentService, SecurityUserDetailsService userDetailsService) {
        this.commentService = commentService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getComment(id);
        if (comment.isPresent()) {
            return ResponseEntity.ok(comment.get());
        } else {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Comment not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @PutMapping("/post/{postId}")
    public ResponseEntity<?> createComment(@PathVariable long postId,
                                           @RequestPart(value = "content") String commentContent,
                                           @RequestPart(value = "image", required = false) MultipartFile file) {
        User user = userDetailsService.getUserByUsername(SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .get();
        Optional<Comment> comment;
        if (!file.isEmpty()) {
            String imageName = FileUtils.generateUniqueName(file.getOriginalFilename());
            try {
                FileUtils.saveFile(Comment.IMAGES_DIRECTORY_PATH, imageName, file);
            } catch (IOException e) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                map.put("message", "Error while uploading Comment image.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
            }
            comment = commentService.commentPost(postId, new Comment(commentContent, imageName, user));
        } else {
            comment = commentService.commentPost(postId, new Comment(commentContent, user));
        }

        if (!comment.isPresent()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Could not find referenced Post.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
        return ResponseEntity.ok(comment.get());
    }

    @DeleteMapping("/{commentId}/post/{postId}")
    public ResponseEntity<?> deleteComment(@PathVariable long postId,
                                           @PathVariable long commentId) {
        Optional<String> commentImageName = commentService.getCommentImageName(commentId);
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
        commentService.deletePostComment(postId, commentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{commentId}/react/{reactionId}")
    public ResponseEntity<?> reactToComment(@PathVariable int reactionId,
                                         @PathVariable long commentId) {
        Optional<Comment> comment = commentService.reactToComment(reactionId, commentId);
        if (!comment.isPresent()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Could not find referenced Comment or Reaction.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
        return ResponseEntity.ok(comment);
    }
}

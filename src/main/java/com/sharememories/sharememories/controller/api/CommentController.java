package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.CommentService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.Optional;

import static com.sharememories.sharememories.util.UserUtils.getLoggedUser;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    private final CommentService commentService;
    private final SecurityUserDetailsService userDetailsService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getComment(id);
        if (comment.isEmpty()) {
            throw new NotFoundException("Comment not found.");
        }

        return ResponseEntity.ok(comment.get());
    }

    @PutMapping("/post/{postId}")
    public ResponseEntity<?> createComment(@PathVariable long postId,
                                           @RequestPart(value = "content") String commentContent,
                                           @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {
        User loggedUser = getLoggedUser(userDetailsService);

        Comment comment = new Comment(commentContent, loggedUser);
        if (!file.isEmpty() && file.getOriginalFilename() != null) {
            String image = FileUtils.generateUniqueName(file.getOriginalFilename());
            comment.setImage(image);

            FileUtils.saveFile(Comment.IMAGES_DIRECTORY_PATH, image, file);
        }

        Optional<Comment> savedComment = commentService.commentPost(postId, comment);
        if (savedComment.isEmpty())
            throw new NotFoundException("Post not found.");

        return ResponseEntity.ok(savedComment.get());
    }

    @DeleteMapping("/{commentId}/post/{postId}")
    public ResponseEntity<?> deleteComment(@PathVariable long postId,
                                           @PathVariable long commentId) throws IOException {
        Optional<Comment> comment = commentService.getComment(commentId);
        if (comment.isEmpty()) {
            throw new NotFoundException("Comment not found.");
        }

        String image = comment.get().getImage();
        if (image != null) {
            FileUtils.deleteFile(Comment.IMAGES_DIRECTORY_PATH, image);
        }
        commentService.deletePostComment(postId, comment.get());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{commentId}/react/{reactionId}")
    public ResponseEntity<?> reactToComment(@PathVariable int reactionId,
                                         @PathVariable long commentId) {
        Optional<Comment> comment = commentService.reactToComment(reactionId, commentId);
        if (comment.isEmpty()) {
            throw new NotFoundException("Could not find comment or reaction.");
        }

        return ResponseEntity.ok(comment);
    }
}

package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.CommentService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.Optional;

import static com.sharememories.sharememories.util.UserUtils.getLoggedUser;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Comment Controller")
public class CommentController {

    private final CommentService commentService;
    private final SecurityUserDetailsService userDetailsService;


    @Operation(summary = "Get a comment by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getComment(id);
        if (comment.isEmpty()) {
            throw new NotFoundException("Comment not found.");
        }
        return ResponseEntity.ok(comment.get());
    }

    @Operation(summary = "Create a comment on the post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment created.",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while saving image",
                    content = @Content)})
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
        if (savedComment.isEmpty()) {
            throw new NotFoundException("Post not found.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment.get());
    }

    @Operation(summary = "Delete comment by comment's and post's ids.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid comment's or post's id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment or post not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while deleting image",
                    content = @Content)})
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

    @Operation(summary = "Append reaction to comment by comment's and reaction's ids.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction appended.",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid comment's or reaction's id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment or reaction not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
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

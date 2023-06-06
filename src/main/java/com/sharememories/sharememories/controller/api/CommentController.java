package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.service.CommentService;
import com.sharememories.sharememories.validation.annotations.ValidFile;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "Comment Controller")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Get a comment by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the comment",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content)})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Comment getComment(@PathVariable Long id) {
        return commentService.getComment(id);
    }

    @Operation(summary = "Create a comment on the post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while saving image",
                    content = @Content)})
    @PutMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@PathVariable long postId,
                                           @RequestPart(value = "content") String commentContent,
                                           @ValidFile @RequestPart(value = "image") MultipartFile file) throws IOException {
        return commentService.commentPost(postId, commentContent, file);
    }

    @Operation(summary = "Delete comment by comment's and post's ids.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid comment's or post's id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment or post not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while deleting image",
                    content = @Content)})
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@PathVariable long commentId) throws IOException {
        commentService.deleteComment(commentId);
    }

    @Operation(summary = "Add reaction to comment by comment's and reaction's ids.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction added",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid comment's or reaction's id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment or reaction not found",
                    content = @Content)})
    @PutMapping("/{commentId}/react/{reactionId}")
    @ResponseStatus(HttpStatus.OK)
    public Comment reactToComment(@PathVariable int reactionId,
                                         @PathVariable long commentId) {
        return commentService.reactToComment(reactionId, commentId);
    }
}

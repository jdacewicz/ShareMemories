package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.PostService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.sharememories.sharememories.util.UserUtils.getLoggedUser;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Post Controller")
@Validated
public class PostController {

    private final PostService postService;
    private final SecurityUserDetailsService userDetailsService;

    @Operation(summary = "Get a post by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the post",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable long id) {
        Optional<Post> post = postService.getPost(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Post not found.");
        }
        return ResponseEntity.ok(post.get());
    }

    @Operation(summary = "Get random posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found posts",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class)) }),
            @ApiResponse(responseCode = "204", description = "No posts found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content)})
    @GetMapping("/random")
    public ResponseEntity<?> getRandomPosts() {
        List<Post> posts = postService.getRandomPosts();
        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Get all posts by user's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found posts",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class)) }),
            @ApiResponse(responseCode = "204", description = "No posts found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUserId(@PathVariable long userId) {
        List<Post> posts = postService.getAllByCreatorId(userId);
        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Create post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while saving image",
                    content = @Content)})
    @PostMapping()
    public ResponseEntity<?> createPost(@RequestPart("content") String content,
                                        @ValidFile @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {
        User loggedUser = getLoggedUser(userDetailsService);
        Post post = new Post(content, loggedUser);
        if(!file.isEmpty() && file.getOriginalFilename() != null) {
            String image = FileUtils.generateUniqueName(file.getOriginalFilename());
            post.setImage(image);

            FileUtils.saveFile(Post.IMAGES_DIRECTORY_PATH, image, file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(post));
    }

    @Operation(summary = "Add reaction to post by post's and reaction's ids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reaction added",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid ids supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post or reaction not found",
                    content = @Content)})
    @PutMapping("/{postId}/react/{reactionId}")
    public ResponseEntity<?> reactToPost(@PathVariable int reactionId,
                                         @PathVariable long postId) {
        Optional<Post> post = postService.reactToPost(reactionId, postId);
        if (post.isEmpty()) {
            throw new NotFoundException("Could not find post or reaction.");
        }
        return ResponseEntity.ok(post.get());
    }


    @Operation(summary = "Delete post by it's id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while deleting image",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable long id) throws IOException {
        Optional<Post> post = postService.getPost(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Post not found.");
        }

        String image = post.get().getImage();
        if (image != null) {
            FileUtils.deleteFile(Post.IMAGES_DIRECTORY_PATH, image);
        }

        postService.deletePost(post.get());
        return ResponseEntity.ok().build();
    }
}

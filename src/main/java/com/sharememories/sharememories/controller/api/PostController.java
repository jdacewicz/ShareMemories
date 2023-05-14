package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.PostService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import com.sharememories.sharememories.validation.annotations.ValidFile;
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
@Validated
public class PostController {

    private final PostService postService;
    private final SecurityUserDetailsService userDetailsService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable long id) {
        Optional<Post> post = postService.getPost(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Post not found.");
        }
        return ResponseEntity.ok(post.get());
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomPosts() {
        List<Post> posts = postService.getRandomPosts();
        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUserId(@PathVariable long userId) {
        List<Post> posts = postService.getAllByCreatorId(userId);
        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(posts);
    }

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

    @PutMapping("/{postId}/react/{reactionId}")
    public ResponseEntity<?> reactToPost(@PathVariable int reactionId,
                                         @PathVariable long postId) {
        Optional<Post> post = postService.reactToPost(reactionId, postId);
        if (post.isEmpty()) {
            throw new NotFoundException("Could not find post or reaction.");
        }
        return ResponseEntity.ok(post.get());
    }

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

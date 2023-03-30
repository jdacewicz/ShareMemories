package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.PostService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {

    private PostService postService;
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    public PostController(PostService postService, SecurityUserDetailsService userDetailsService) {
        this.postService = postService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable long id) {
        Optional<Post> post = postService.getPost(id);
        if (post.isPresent()) {
            return ResponseEntity.ok(post.get());
        } else {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Post not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomPosts() {
        List<Post> posts = postService.getRandomPosts();
        if (!posts.isEmpty())
            return ResponseEntity.ok(posts);
         else
            return ResponseEntity.noContent().build();
    }

    @PostMapping()
    public ResponseEntity<?> createPost(@RequestPart("content") String content,
                                        @RequestPart(value = "image", required = false) MultipartFile file) {
        User user = userDetailsService.getUserByUsername(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName())
                .get();
        Post post;
        if (!file.isEmpty()) {
            String imageName = FileUtils.generateUniqueName(file.getOriginalFilename());
            try {
                FileUtils.saveFile(Post.IMAGES_DIRECTORY_PATH, imageName, file);
            } catch (IOException e) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                map.put("message", "Error while uploading Reaction image.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
            }
            post = postService.createPost(new Post(content, imageName, user));
        } else {
            post = postService.createPost(new Post(content, user));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @PutMapping("/{postId}/comment")
    public ResponseEntity<?> createComment(@PathVariable long postId,
                                           @RequestPart(value = "content") String commentContent,
                                           @RequestPart(value = "image", required = false) MultipartFile file) {
        Optional<Post> post;
        if (!file.isEmpty()) {
            String imageName = FileUtils.generateUniqueName(file.getOriginalFilename());
            try {
                FileUtils.saveFile(Comment.IMAGES_DIRECTORY_PATH, imageName, file);
            } catch (IOException e) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                map.put("message", "Error while uploading Reaction image.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
            }
            post = postService.commentPost(postId, new Comment(commentContent, imageName));
        } else {
            post = postService.commentPost(postId, new Comment(commentContent));
        }

        if (!post.isPresent()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Could not find referenced Post.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
        return ResponseEntity.ok(post.get());
    }

    @PutMapping("/{postId}/react/{reactionId}")
    public ResponseEntity<?> reactToPost(@PathVariable int reactionId,
                                         @PathVariable long postId) {
        Optional<Post> post = postService.reactToPost(reactionId, postId);
        if (!post.isPresent()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Could not find referenced Post or Reaction.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
        return ResponseEntity.ok(post.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        Optional<String> postImageName = postService.getPostImageName(id);
        if (postImageName.isPresent()) {
            try {
                FileUtils.deleteFile(Post.IMAGES_DIRECTORY_PATH, postImageName.get());
            } catch (IOException e) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                map.put("message", "Error while deleting Post image.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
            }
        }
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}

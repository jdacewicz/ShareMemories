package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {

    private PostService service;

    @Autowired
    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Optional<Post> get(@PathVariable Long id) {
        return service.getPost(id);
    }

    @GetMapping("/random")
    public List<Post> getRandom() {
        return service.getRandomPosts();
    }

    @PostMapping()
    public Post create(@RequestPart("content") String content, @RequestPart("image") MultipartFile file) {
        return service.createPost(content, file);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deletePost(id);
    }

    @PutMapping("/{id}/comment")
    public Optional<Post> createComment(@PathVariable Long id, @RequestPart("content") String content, @RequestPart("image") MultipartFile file) {
        return service.commentPost(id, content, file);
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public void deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        service.deletePostComment(postId, commentId);
    }
}

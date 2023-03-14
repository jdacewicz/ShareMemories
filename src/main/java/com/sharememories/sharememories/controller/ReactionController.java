package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/reactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReactionController {

    private ReactionService service;

    @Autowired
    public ReactionController(ReactionService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Optional<Reaction> get(@PathVariable Integer id) {
        return service.getReaction(id);
    }

    @GetMapping()
    public List<Reaction> getAll() {
        return service.getAllReactions();
    }

    @PostMapping()
    public Reaction create(@RequestPart("name") String name, @RequestPart("image") MultipartFile file) {
        return service.createReaction(name, file);
    }

    @PutMapping("/{id}")
    public Reaction replace(@RequestPart("name") String name, @RequestPart("image") MultipartFile file, @PathVariable Integer id) {
        return service.replaceReaction(name, file, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteReaction(id);
    }

    @PutMapping("/{reactionId}/posts/{postId}")
    public void updateReactions(@PathVariable Integer reactionId, @PathVariable Long postId) {
        service.reactToPost(reactionId, postId);
    }
}

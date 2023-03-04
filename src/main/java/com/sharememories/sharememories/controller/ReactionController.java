package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public Reaction create(@RequestBody Reaction newReaction) {
        return service.createReaction(newReaction);
    }

    @PutMapping("/{id}")
    public Reaction replace(@RequestBody Reaction newReaction, @PathVariable Integer id) {
        return service.replaceReaction(id, newReaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteReaction(id);
    }
}

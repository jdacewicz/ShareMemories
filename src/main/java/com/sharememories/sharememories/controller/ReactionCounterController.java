package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.ReactionCounter;
import com.sharememories.sharememories.service.ReactionCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/reactions/counters", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReactionCounterController {

    private ReactionCounterService service;

    @Autowired
    public ReactionCounterController(ReactionCounterService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Optional<ReactionCounter> get(@PathVariable Long id) {
        return service.getReactionCounter(id);
    }

    @PostMapping()
    public ReactionCounter create(@RequestBody ReactionCounter newReactionCounter) {
        return service.createReactionCounter(newReactionCounter);
    }

    @PutMapping("/{id}")
    public ReactionCounter replace(@RequestBody ReactionCounter newReactionCounter, @PathVariable Long id) {
        return service.replaceReactionCounter(id, newReactionCounter);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteReactionCounter(id);
    }
}

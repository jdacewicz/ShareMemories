package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/reactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReactionController {

    private ReactionService service;

    @Autowired
    public ReactionController(ReactionService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<?> getAllReactions() {
        List<Reaction> reaction = service.getAllReactions();
        if (!reaction.isEmpty()) {
            return ResponseEntity.ok(reaction);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReaction(@PathVariable Integer id) {
        Optional<Reaction> reaction = service.getReaction(id);
        if (reaction.isPresent()) {
            return ResponseEntity.ok(reaction.get());
        } else {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Reaction not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @PostMapping()
    public ResponseEntity<?> createReaction(@RequestPart("name") String name,
                                            @RequestPart("image") MultipartFile file) {
        Reaction reaction = service.createReaction(name, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceReaction(@PathVariable(value = "id") Integer id,
                                             @RequestPart(value = "name") String name,
                                             @RequestPart(value = "image", required = false) MultipartFile file) {
        if (file.isEmpty()) {
            Reaction reaction = service.replaceReaction(new Reaction(id, name));
            return ResponseEntity.ok(reaction);
        } else {
            Optional<Reaction> reaction = service.replaceReaction(new Reaction(id, name), file);
            if (reaction.isPresent()) {
                return ResponseEntity.ok(reaction.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReaction(@PathVariable("id") Integer id) {
        boolean isDeleted = service.deleteReaction(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Reaction not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }
}

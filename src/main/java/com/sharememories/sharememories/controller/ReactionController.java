package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.service.ReactionService;
import com.sharememories.sharememories.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

        return (reaction.isEmpty()) ?
                ResponseEntity.noContent().build() : ResponseEntity.ok(reaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReaction(@PathVariable Integer id) {
        Optional<Reaction> reaction = service.getReaction(id);
        if (!reaction.isPresent()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Reaction not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        } else {
            return ResponseEntity.ok(reaction.get());
        }
    }

    @PostMapping()
    public ResponseEntity<?> createReaction(@RequestPart("name") String name,
                                            @RequestPart("image") MultipartFile file) {
        String imageName = FileUtils.generateUniqueName(file.getOriginalFilename());
        try {
            FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, imageName, file);
        } catch (IOException e) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            map.put("message", "Error while uploading Reaction image.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }

        Reaction reaction = service.createReaction(new Reaction(name, imageName));
        return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceReaction(@PathVariable(value = "id") int id,
                                             @RequestPart(value = "name") String name,
                                             @RequestPart(value = "imageName", required = false) String imageName,
                                             @RequestPart(value = "image", required = false) MultipartFile file) {
        Reaction reaction;
        if (!file.isEmpty()) {
            String fileName = (!imageName.isEmpty()) ?
                    imageName : FileUtils.generateUniqueName(file.getOriginalFilename());
            try {
                FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, fileName, file);
            } catch (IOException e) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                map.put("message", "Error while replacing Reaction image.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
            }
            reaction = service.replaceReaction(id, new Reaction(name, fileName));
        } else {
            reaction = service.replaceReaction(id, new Reaction(name));
        }
        return ResponseEntity.ok(reaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReaction(@PathVariable("id") Integer id) {
//        Optional<Reaction> reaction = service.deleteReaction(id);
//        if (!reaction.isPresent()) {
//            return ResponseEntity.ok().build();
//        } else {
//            Map<String, Object> map = new LinkedHashMap<>();
//            map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
//            map.put("message", "Error while deleting Reaction.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
//        }
        return ResponseEntity.ok().build();
    }
}

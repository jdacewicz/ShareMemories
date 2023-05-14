package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.service.ReactionService;
import com.sharememories.sharememories.util.FileUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/reactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReactionController {

    private final ReactionService service;


    @GetMapping()
    public ResponseEntity<?> getAllReactions() {
        List<Reaction> reaction = service.getAllReactions();
        if (reaction.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReaction(@PathVariable Integer id) {
        Optional<Reaction> reaction = service.getReaction(id);
        if (reaction.isEmpty()) {
            throw new NotFoundException("Reaction not found.");
        }
        return ResponseEntity.ok(reaction.get());
    }

    @PostMapping()
    public ResponseEntity<?> createReaction(@RequestPart("name") String name,
                                            @RequestPart("image") MultipartFile file) throws IOException {
        String imageName = FileUtils.generateUniqueName(file.getOriginalFilename());
        FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, imageName, file);

        Reaction reaction = service.createReaction(new Reaction(name, imageName));
        return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceReaction(@PathVariable(value = "id") int id,
                                             @RequestPart(value = "name") String name,
                                             @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {
        Optional<Reaction> reaction = service.getReaction(id);
        if (reaction.isEmpty()) {
            throw new NotFoundException("Reaction not found.");
        }
        reaction.get().setName(name);

        if (!file.isEmpty() && file.getOriginalFilename() != null) {
            String image = reaction.get().getImage();
            if (image == null) {
                image = FileUtils.generateUniqueName(file.getOriginalFilename());
                reaction.get().setImage(image);
            }
            FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, image, file);
        }
        return ResponseEntity.ok(service.replaceReaction(id, reaction.get()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReaction(@PathVariable("id") Integer id) throws IOException {
        Optional<Reaction> reaction = service.getReaction(id);
        if (reaction.isEmpty()) {
            throw new NotFoundException("Reaction not found.");
        }

        if (reaction.get().getImage() != null) {
            FileUtils.deleteFile(Reaction.IMAGES_DIRECTORY_PATH, reaction.get().getImage());
        }
        service.deleteReaction(reaction.get());

        return ResponseEntity.ok().build();
    }
}

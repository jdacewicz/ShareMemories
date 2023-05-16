package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.service.ReactionService;
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

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/reactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reaction Controller")
@Validated
public class ReactionController {

    private final ReactionService service;


    @Operation(summary = "Get all reactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found reactions",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Reaction.class)) }),
            @ApiResponse(responseCode = "204", description = "No reactions found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content)})
    @GetMapping()
    public ResponseEntity<?> getAllReactions() {
        List<Reaction> reaction = service.getAllReactions();
        if (reaction.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reaction);
    }

    @Operation(summary = "Get a reaction by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the reaction",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Reaction.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reaction not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<?> getReaction(@PathVariable Integer id) {
        Optional<Reaction> reaction = service.getReaction(id);
        if (reaction.isEmpty()) {
            throw new NotFoundException("Reaction not found.");
        }
        return ResponseEntity.ok(reaction.get());
    }

    @Operation(summary = "Create reaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reaction created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Reaction.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while saving image",
                    content = @Content)})
    @PostMapping()
    public ResponseEntity<?> createReaction(@RequestPart("name") String name,
                                            @ValidFile @RequestPart("image") MultipartFile file) throws IOException {
        Reaction reaction = new Reaction(name);
        if (file.getOriginalFilename() != null) {
            String image = FileUtils.generateUniqueName(file.getOriginalFilename());
            reaction.setImage(image);

            FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, image, file);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.createReaction(reaction));
    }

    @Operation(summary = "Update reaction by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Reaction.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reaction not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while saving image",
                    content = @Content)})
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

    @Operation(summary = "Delete reaction by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reaction deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reaction not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while deleting image",
                    content = @Content)})
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

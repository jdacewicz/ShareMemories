package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static com.sharememories.sharememories.util.UserUtils.getLoggedUser;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User Controller")
public class UserController {

    private final SecurityUserDetailsService userDetailsService;


    @Operation(summary = "Get a user by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id ){
        Optional<User> user = userDetailsService.getUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found.");
        }
        return ResponseEntity.ok(user.get());
    }

    @Operation(summary = "Get all unknown contacts",
            description = "Get all users that send message but aren't in logged user contacts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }),
            @ApiResponse(responseCode = "204", description = "No users found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content)})
    @GetMapping("/contacts/unknown")
    public ResponseEntity<?> getAllUnknownMessageSenders() {
        User loggedUser = getLoggedUser(userDetailsService);
        Set<User> senders = userDetailsService.getAllUnknownMessageSenders(loggedUser, false);
        if (senders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(senders);
    }

    @Operation(summary = "Get all saved contacts",
            description = "Get all users from logged user contacts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found users",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }),
            @ApiResponse(responseCode = "204", description = "No users found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content)})
    @GetMapping("/contacts")
    public ResponseEntity<?> getAllContacts() {
        User loggedUser = getLoggedUser(userDetailsService);
        Set<User> contacts = loggedUser.getContacts();
        if (contacts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(contacts);
    }

    @Operation(summary = "Add user to logged user's contacts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User invited",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Invited user not found",
                    content = @Content)})
    @PutMapping("/invite/{invitedUserId}")
    public ResponseEntity<?> addUserToFriends(@PathVariable long invitedUserId) {
        User user = getLoggedUser(userDetailsService);
        Optional<User> output = userDetailsService.addUserToFriendsList(user, invitedUserId);
        if (output.isEmpty()) {
            throw new NotFoundException("Invited user not found.");
        }
        return ResponseEntity.ok(output.get().getContacts());
    }

    @Operation(summary = "Delete user by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while deleting image",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) throws IOException {
        Optional<User> user = userDetailsService.getUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        String image = user.get().getProfileImage();
        if (image != null) {
            FileUtils.deleteFile(User.IMAGES_DIRECTORY_PATH, image);
        }

        userDetailsService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}

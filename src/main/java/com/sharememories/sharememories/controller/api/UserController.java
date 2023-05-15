package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
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
public class UserController {

    private final SecurityUserDetailsService userDetailsService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id ){
        Optional<User> user = userDetailsService.getUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found.");
        }
        return ResponseEntity.ok(user.get());
    }

    @GetMapping("/contacts/unknown")
    public ResponseEntity<?> getAllUnknownMessageSenders() {
        User loggedUser = getLoggedUser(userDetailsService);
        Set<User> senders = userDetailsService.getAllUnknownMessageSenders(loggedUser, false);
        if (senders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(senders);
    }

    @GetMapping("/contacts")
    public ResponseEntity<?> getAllContacts() {
        User loggedUser = getLoggedUser(userDetailsService);
        Set<User> contacts = loggedUser.getContacts();
        if (contacts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(contacts);
    }

    @PutMapping("/invite/{invitedUserId}")
    public ResponseEntity<?> addUserToFriends(@PathVariable long invitedUserId) {
        User user = getLoggedUser(userDetailsService);
        Optional<User> output = userDetailsService.addUserToFriendsList(user, invitedUserId);
        if (output.isEmpty()) {
            throw new NotFoundException("Invited user not found.");
        }
        return ResponseEntity.ok(output.get());
    }

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
